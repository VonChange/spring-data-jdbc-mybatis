
package com.vonchange.spring.data.mybatis.mini.jdbc.repository.config;

import com.vonchange.spring.data.mybatis.mini.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

import java.util.Locale;




public class JdbcRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {



	@Override
	public String getModuleName() {
		return "JDBC";
	}

	public String getRepositoryFactoryClassName() {
		return JdbcRepositoryFactoryBean.class.getName();
	}

	public String getRepositoryFactoryBeanClassName() {
		return JdbcRepositoryFactoryBean.class.getName();
	}

	@Override
	protected String getModulePrefix() {
		return getModuleName().toLowerCase(Locale.US);
	}


}
