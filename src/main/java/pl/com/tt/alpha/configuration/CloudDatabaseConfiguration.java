package pl.com.tt.alpha.configuration;

import io.github.jhipster.config.JHipsterConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Slf4j
@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_CLOUD)
public class CloudDatabaseConfiguration extends AbstractCloudConfig {

	@Bean
	public DataSource dataSource(CacheManager cacheManager) {
		log.info("Configuring JDBC datasource from a cloud provider");
		return connectionFactory().dataSource();
	}
}
