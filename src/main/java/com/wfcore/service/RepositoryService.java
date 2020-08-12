package com.wfcore.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
import com.hazelcast.core.IList;
import com.wfcore.model.Repository;
import com.wfcore.utility.Constants;

@CacheConfig(cacheNames = Constants.REPOSITORIES)
@Service
public class RepositoryService {

	public List<Repository> findAllRepos(String keyword) {
		List<Repository> repositories = retrieveRepoFromCache();

		if (keyword == null) {
			return repositories;
		} else {
			return retrieveRepositoriesWhichContainsKeyword(keyword, repositories);
		}
	}

	/*
	 * Call to github api.
	 */
	@Cacheable()
	private List<Repository> retrieveAllPublicRepositories() {

		List<Repository> repositories = new ArrayList<>();

		String url = "https://api.github.com/repositories";
		try (CloseableHttpClient httpClient = new DefaultHttpClient()) {
			URIBuilder builder = new URIBuilder(url);

			URI uri = builder.build();
			HttpGet getRequest = new HttpGet(uri);
			getRequest.addHeader(Constants.ACCEPT, Constants.APPLICATION_JSON);
			HttpResponse response = httpClient.execute(getRequest);

			ObjectMapper mapper = new ObjectMapper();

			TypeReference<List<Repository>> typeReference = new TypeReference<List<Repository>>() {
			};
			repositories = mapper.readValue(response.getEntity().getContent(), typeReference);

			IList<Repository> repoFromCache = Hazelcast.getHazelcastInstanceByName(Constants.HAZELCAST_INSTANCE)
					.getList(Constants.REPOSITORIES);
			repoFromCache.addAll(repositories);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return repositories;
	}

	private List<Repository> retrieveRepositoriesWhichContainsKeyword(String keyword, List<Repository> repo) {

		List<Repository> repositories = repo.stream().filter(r -> r.getName().contains(keyword)
				|| r.getFull_name().contains(keyword) || r.getOwner().getLogin().contains(keyword))
				.collect(Collectors.toList());

		return repositories;
	}

	public Repository findById(String id) {

		return retrieveRepositoryById(id, retrieveRepoFromCache());

	}

	private Repository retrieveRepositoryById(String id, List<Repository> repo) {

		return repo.stream().filter(r -> r.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	/*
	 * Retrieve data from cache. If not, call github api.
	 */
	private List<Repository> retrieveRepoFromCache() {

		List<Repository> repositories = new ArrayList<>();
		HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName(Constants.HAZELCAST_INSTANCE);

		if (hazelcastInstance != null) {
			IList<Repository> repoFromCache = hazelcastInstance.getList(Constants.REPOSITORIES);
			if (repoFromCache != null && !repoFromCache.isEmpty()) {

				System.out.print("REPO FROM CACHE");
				return repoFromCache;

			} else {
				repositories = retrieveAllPublicRepositories();
			}
		}

		return repositories;
	}
}
