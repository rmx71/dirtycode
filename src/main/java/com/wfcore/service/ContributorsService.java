package com.wfcore.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.wfcore.model.Contributor;
import com.wfcore.model.Repository;
import com.wfcore.utility.Constants;

@CacheConfig(cacheNames = Constants.CONTRIBUTORS)
@Service
public class ContributorsService {

	@Autowired
	RepositoryService service;

	public List<String> findContributorsList(Repository repo) {
		List<String> cNameList = new ArrayList<String>();

		List<Contributor> cList = retrieveContributorsFromCache(repo);
		cList.stream().forEach(contributor -> cNameList.add(contributor.getLogin()));

		return cNameList;

	}

	@Cacheable()
	private List<Contributor> retrieveCommitters(Repository repo) {

		List<Contributor> cList = new ArrayList<>();
		try (CloseableHttpClient httpClient = new DefaultHttpClient()) {
			URIBuilder builder = new URIBuilder(repo.getContributors_url());

			URI uri = builder.build();
			HttpGet getRequest = new HttpGet(uri);
			getRequest.addHeader(Constants.ACCEPT, Constants.APPLICATION_JSON);
			HttpResponse response = httpClient.execute(getRequest);

			ObjectMapper mapper = new ObjectMapper();

			TypeReference<List<Contributor>> typeReference = new TypeReference<List<Contributor>>() {
			};
			cList = mapper.readValue(response.getEntity().getContent(), typeReference);

			IMap<String, List<Contributor>> repoFromCache = Hazelcast
					.getHazelcastInstanceByName(Constants.HAZELCAST_INSTANCE).getMap(Constants.CONTRIBUTORS);
			repoFromCache.put(repo.getName(), cList);

			System.out.print(repoFromCache.getEntryView(repo.getName()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cList;
	}

	private List<Contributor> retrieveContributorsFromCache(Repository repo) {
		List<Contributor> contributors = new ArrayList<>();
		HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName(Constants.HAZELCAST_INSTANCE);

		if (hazelcastInstance != null) {
			IMap<String, List<Contributor>> repoFromCache = hazelcastInstance.getMap(Constants.CONTRIBUTORS);
			if (repoFromCache != null && !repoFromCache.isEmpty()) {
				for (IMap.Entry<String, List<Contributor>> entry : repoFromCache.entrySet()) {
					if (entry.getKey().equalsIgnoreCase(repo.getName())) {
						contributors.addAll(entry.getValue());
					} else {
						contributors = retrieveCommitters(repo);
					}
				}
				System.out.print("CONTRIBUTOR FROM CACHE");
			} else {
				contributors = retrieveCommitters(repo);
			}
		}

		return contributors;
	}
}
