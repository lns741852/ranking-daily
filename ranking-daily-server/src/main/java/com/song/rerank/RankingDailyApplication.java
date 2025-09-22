package com.song.rerank;

import com.song.rerank.utils.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 開啟審計功能 -> @EnableJpaAuditing 開啟底下功能
 * 		 -> @CreatedDate
 * 		 -> @CreatedBy
 * 		 -> @LastModifiedDate
 * 		 -> @LastModifiedBy
 * 開啟事務功能 -> @EnableTransactionManagement
 * 開啟異步功能 -> @EnableAsync
 */
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class RankingDailyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RankingDailyApplication.class, args);
	}

	@Bean
	public SpringContextHolder springContextHolder(){
		return  new SpringContextHolder();
	}

}
