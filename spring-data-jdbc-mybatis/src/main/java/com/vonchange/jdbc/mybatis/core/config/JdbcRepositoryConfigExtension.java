
package com.vonchange.jdbc.mybatis.core.config;

import com.vonchange.jdbc.mybatis.core.support.JdbcRepositoryFactoryBean;
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
