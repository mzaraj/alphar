package pl.com.tt.alpha.security.configuration;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("spring.datasource")
@Getter
@Setter
public class JdbcConfiguration {
	private String url;
	private String username;
	private String password;
	private String driverClassName;

	@Bean
	public DataSource getDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(getDriverClassName());
		dataSource.setUrl(getUrl());
		dataSource.setUsername(getUsername());
		dataSource.setPassword(getPassword());
		return dataSource;
	}
}
