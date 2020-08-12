package com.wfcore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.wfcore.utility.Constants;

@Configuration
public class HazelcastConfiguration {

	@Bean
	public Config hazelCastConfig() {

		return new Config().setInstanceName(Constants.HAZELCAST_INSTANCE)
				.addListConfig(new ListConfig(Constants.REPOSITORIES).setBackupCount(2)
						.setMaxSize(MaxSizeConfig.DEFAULT_MAX_SIZE))
				.addMapConfig(new MapConfig(Constants.CONTRIBUTORS).setEvictionPolicy(EvictionPolicy.LFU)
						.setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
						.setTimeToLiveSeconds(1000))
				.addMapConfig(new MapConfig(Constants.COMMITS).setEvictionPolicy(EvictionPolicy.LFU)
						.setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
						.setTimeToLiveSeconds(1000));

	}

}
