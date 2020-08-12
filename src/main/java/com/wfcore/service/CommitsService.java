package com.wfcore.service;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.wfcore.model.Commits;
import com.wfcore.model.Repository;
import com.wfcore.utility.Constants;

@CacheConfig(cacheNames = Constants.COMMITS)
@Service
public class CommitsService {

	/***
	 * Retrieve last 100 committers.
	 * 
	 * @param repo
	 * @return Map<Committer, # of Commits>
	 */
	public Map<String, Integer> retrieve100LastCommitters(Repository repo) {

		List<Commits> commits = retrieveCommitsByRepository(repo);
		List<String> cNameList = new ArrayList<String>();
		Map<String, Integer> cMap = new HashMap<>();

		for (Commits commit : commits) {
			cNameList.add(commit.getCommit().getCommitter().getName());
		}

		Object[] cArray = cNameList.toArray();

		Arrays.stream(cArray).collect(Collectors.groupingBy(a -> a)).forEach((k, v) -> cMap.put((String) k, v.size()));

		return cMap;
	}

	/*
	 * Convert String to LocalDateTime.
	 */
	private LocalDateTime formatDate(String date) {

		DateTimeFormatter input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		return LocalDateTime.parse(date, input);
	}

	/***
	 * Get last 100 commit details.
	 * 
	 * @param repo
	 * @return Map<Date, Committer>
	 */
	public Map<LocalDateTime, String> getCommitDetails(Repository repo) {

		List<Commits> last100Commits = retrieveCommitsByRepository(repo);
		Map<LocalDateTime, String> commitMap = new TreeMap<LocalDateTime, String>((Collections.reverseOrder()));

		for (Commits commits : last100Commits) {
			LocalDateTime date = formatDate(commits.getCommit().getCommitter().getDate());

			commitMap.put(date, commits.getCommit().getCommitter().getName());
		}
		return commitMap;

	}

	/*
	 * Retrieve last 100 commits.
	 */
	private List<Commits> retrieveCommitsByRepository(Repository repo) {
		List<Commits> last100Commits = new ArrayList<>();
		List<Commits> cList = retrieveContributorsFromCache(repo);

		if (cList.size() > 100) {
			for (Commits commits : cList) {
				last100Commits.add(commits);
				if (last100Commits.size() == 100) {
					break;
				}
			}
			return last100Commits;
		}
		return cList;
	}

	/*
	 * Get commits list from github.
	 */
	@Cacheable()
	private List<Commits> retrieveCommitsList(Repository repo) {
		List<Commits> cList = new ArrayList<>();
		// "https://api.github.com/repos/{owner}/{repo}/commits";
		try (CloseableHttpClient httpClient = new DefaultHttpClient()) {

			URIBuilder builder = new URIBuilder().setScheme("https").setHost("api.github.com")
					.setPath("/repos/" + repo.getOwner().getLogin() + "/" + repo.getName() + "/commits");

			URI uri = builder.build();

			HttpGet getRequest = new HttpGet(uri);
			getRequest.addHeader(Constants.ACCEPT, Constants.APPLICATION_JSON);

			HttpResponse response = httpClient.execute(getRequest);

			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<Commits>> typeReference = new TypeReference<List<Commits>>() {
			};

			cList = mapper.readValue(response.getEntity().getContent(), typeReference);

			IMap<String, List<Commits>> repoFromCache = Hazelcast
					.getHazelcastInstanceByName(Constants.HAZELCAST_INSTANCE).getMap(Constants.COMMITS);
			repoFromCache.put(repo.getName(), cList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cList;
	}

	/*
	 * Retrieve last 100 commits.If cache is empty, retrieve from github api.
	 */
	private List<Commits> retrieveContributorsFromCache(Repository repo) {
		List<Commits> commits = new ArrayList<>();
		HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName(Constants.HAZELCAST_INSTANCE);

		if (hazelcastInstance != null) {
			IMap<String, List<Commits>> repoFromCache = hazelcastInstance.getMap(Constants.COMMITS);
			if (repoFromCache != null && !repoFromCache.isEmpty()) {
				for (IMap.Entry<String, List<Commits>> entry : repoFromCache.entrySet()) {
					if (entry.getKey().equalsIgnoreCase(repo.getName())) {
						commits.addAll(entry.getValue());
					} else {
						commits = retrieveCommitsList(repo);
					}
				}
				System.out.print("COMMITS FROM CACHE");
			} else {
				commits = retrieveCommitsList(repo);
			}
		}

		return commits;
	}
}
