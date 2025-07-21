package fei.song.openrtb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * MongoDB 配置类
 * 配置 MongoDB 连接、编解码器和自定义转换器
 */
@Configuration
@EnableMongoRepositories(basePackages = "fei.song.openrtb.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database:openrtb}")
    private String databaseName;

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/openrtb}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        // 配置编解码器注册表
        CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        // 构建 MongoDB 客户端设置
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(mongoUri))
            .codecRegistry(pojoCodecRegistry)
            .applyToConnectionPoolSettings(builder -> 
                builder.maxSize(20)
                       .minSize(5)
                       .maxWaitTime(2000, TimeUnit.MILLISECONDS)
                       .maxConnectionIdleTime(30000, TimeUnit.MILLISECONDS)
            )
            .applyToSocketSettings(builder -> 
                builder.connectTimeout(5000, TimeUnit.MILLISECONDS)
                       .readTimeout(10000, TimeUnit.MILLISECONDS)
            )
            .build();

        return MongoClients.create(settings);
    }

    @Override
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
            // 可以在这里添加自定义转换器
        ));
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}