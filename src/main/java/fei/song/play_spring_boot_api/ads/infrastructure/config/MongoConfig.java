package fei.song.play_spring_boot_api.ads.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import fei.song.play_spring_boot_api.config.MongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * MongoDB 配置类
 */
@Configuration
@EnableMongoRepositories(basePackages = "fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository")
@ConditionalOnProperty(name = "mongo.enabled", havingValue = "true", matchIfMissing = true)
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Autowired
    private MongoProperties mongoProperties;

    @Override
    @NonNull
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }

    @Override
    @NonNull
    public MongoClient mongoClient() {
        ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder()
                 .maxSize(mongoProperties.getConnectionPool().getMaxSize())
                 .minSize(mongoProperties.getConnectionPool().getMinSize())
                 .maxWaitTime(mongoProperties.getConnectionPool().getMaxWaitTimeSeconds(), TimeUnit.SECONDS)
                 .maxConnectionIdleTime(mongoProperties.getConnectionPool().getMaxConnectionIdleTimeSeconds(), TimeUnit.SECONDS)
                 .maxConnectionLifeTime(mongoProperties.getConnectionPool().getMaxConnectionLifeTimeSeconds(), TimeUnit.SECONDS)
                 .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoProperties.getUri()))
                .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                .build();

        return MongoClients.create(settings);
    }

    /**
     * 自定义 MongoTemplate，移除 _class 字段
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), getDatabaseName());
        
        // 获取转换器并移除 _class 字段
        MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        
        return mongoTemplate;
    }

    /**
     * 自定义转换器配置
     */
    @Bean
    @NonNull
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }

    /**
     * MongoDB 映射上下文配置
     */
    @Bean
    public MongoMappingContext mongoMappingContext() {
        MongoMappingContext context = new MongoMappingContext();
        context.setAutoIndexCreation(true);
        return context;
    }
}