package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(@Value("${vcap.services}") String vcapServicesJson) {
        return new DatabaseServiceCredentials(vcapServicesJson);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        //MysqlDataSource dataSource = new MysqlDataSource();
        //dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(serviceCredentials.jdbcUrl("albums-mysql"));
        return ds;
    }

    @Bean
    public HikariDataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        //MysqlDataSource dataSource = new MysqlDataSource();
        //dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(serviceCredentials.jdbcUrl("movies-mysql"));
        return ds;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaAutoConfiguration(){

        HibernateJpaVendorAdapter vendorAdapter =  new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        vendorAdapter.setDatabase(Database.MYSQL);

        return vendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean(DataSource moviesDataSource, HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(moviesDataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(adapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("moviesDB");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(DataSource albumsDataSource, HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(albumsDataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(adapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("albumsDB");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager albumsPlatformTransactionManager(EntityManagerFactory albumsEntityManagerFactoryBean){

        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setPersistenceUnitName("albumsJPA");
        jpaTransactionManager.setEntityManagerFactory(albumsEntityManagerFactoryBean);
        return jpaTransactionManager;
    }

    @Bean
    public PlatformTransactionManager moviesPlatformTransactionManager(EntityManagerFactory moviesEntityManagerFactoryBean){

        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setPersistenceUnitName("moviesJPA");
        jpaTransactionManager.setEntityManagerFactory(moviesEntityManagerFactoryBean);
        return jpaTransactionManager;
    }
}
