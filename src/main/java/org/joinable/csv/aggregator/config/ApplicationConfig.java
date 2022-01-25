package org.joinable.csv.aggregator.config;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

//@Configuration
//@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "application")
//@ConfigurationProperties
public class ApplicationConfig {


    //Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    public static final String TESTING_MEMORY = "2147480000";
    public static final String DRIVER_MEMORY = "571859200";
    public static final String MAX_RESULT_SIZE = "2147480000";


    @Autowired
    private Environment env;

    @Value("${app.name:csvaggregator}")
    private String appName;

//    @Value("${spark.home}")
//    private String sparkHome;

    @Value("${master.uri:local}")
    private String masterUri;

    @Value("${base.dir.resources:src/main/resources/}")
    private String resourcesDir;

    @Value("${base.dir.small:src/main/resources/small_example/}")
    private String smallFilesDir;

    @Value("${base.dir.medium:src/main/resources/medium_example/}")
    private String mediumFilesDir;

    @Value("${base.dir.volume:vol/}")
    private String dirVolume;

    @Bean
    public SparkConf sparkConf() {
        SparkConf sparkConf = new SparkConf()
                .setAppName(appName)
                //.setSparkHome(sparkHome)
                .setMaster(masterUri);

        sparkConf.set("spark.testing.memory", TESTING_MEMORY);
        sparkConf.set("spark.driver.memory", DRIVER_MEMORY);
        sparkConf.set("spark.driver.maxResultSize", MAX_RESULT_SIZE);

        return sparkConf;
    }

    @Bean
    public JavaSparkContext javaSparkContext() {

        //JavaSparkContext ctx = JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(conf));
        return JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(sparkConf()));
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .sparkContext(javaSparkContext().sc())
                .appName("Java Spark CSV Aggregator")
                .getOrCreate();
    }


    @PostConstruct
    public void init() {
        //log.info("Init spark");
    }

    @PreDestroy
    public void destroy() {
        //logger.info("Destroy spark");
        javaSparkContext().stop();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    public String getSmallFilesDir()
    {
        return smallFilesDir;
    }

    public String getMediumFilesDir()
    {
        return mediumFilesDir;
    }

    public String getResourcesDir()
    {
        return resourcesDir;
    }

    public String getDirVolume()
    {
        return dirVolume;
    }

}