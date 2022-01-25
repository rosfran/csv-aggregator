package org.joinable.csv.aggregator.service;


import org.joinable.csv.aggregator.config.ApplicationConfig;
import org.joinable.csv.aggregator.service.types.FileTypeEnum;
import org.joinable.csv.aggregator.util.FilesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


@Component
public class CSVAggregatorService
{

    //Logger logger = LoggerFactory.getLogger(CSVAggregatorService.class);

    Logger logger = LoggerFactory.getLogger(CSVAggregatorService.class);

    @Autowired
    private ApplicationConfig config;

    SparkConf conf;

    Dataset<Row> resultSpark;

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private JavaSparkContext javaSparkContext;

    public void stopSpark()
    {
        if (sparkSession != null)
        {
            sparkSession.stop();
        }
    }

    public Dataset<Row> aggregateMediumCSV( String arqOut )
    {

        long st = System.currentTimeMillis();

        logger.info("Starting aggregateCSV... = "+st);

        Dataset<Row> ds = aggregateCSV( arqOut, FileTypeEnum.MEDIUM);

        logger.info("Stopping aggregateMediumCSV... = "+(System.currentTimeMillis() - st ) / 1000 );

        return ds;

    }

    public Dataset<Row> aggregateSmallCSV( String arqOut )
    {

        long st = System.currentTimeMillis();

        logger.info("Starting aggregateSmallCSV... = "+st);

        Dataset<Row> ds = aggregateCSV( arqOut, FileTypeEnum.SMALL);

        logger.info("Stopping aggregateSmallCSV... = "+(System.currentTimeMillis() - st ) / 1000 );

        return ds;

    }

    public Dataset<Row> aggregateCSV( String arqOut, FileTypeEnum type )
    {

        String baseDir = "";

        if ( type == FileTypeEnum.MEDIUM )
        {
            baseDir = config.getMediumFilesDir();
        } else if ( type == FileTypeEnum.SMALL )
        {
            baseDir = config.getSmallFilesDir();
        }

        Dataset<Row> res =  sparkSession.read().format("csv").option("header", "false").load(baseDir+"/*").
                repartition(20).
                orderBy(functions.asc("_c0"));

        res.explain(true);
        res.printSchema();
        logger.info("list.size() == "+res.collectAsList().size());
        FilesUtil.deleteIfExists(config.getDirVolume()+"/out/tmp_"+arqOut+"t");

        res.coalesce(1).write().format("csv").mode("overwrite").save(config.getDirVolume()+"/out/tmp_"+arqOut+"t" );

        FilesUtil.copyToBaseDir(config.getDirVolume()+"/out/tmp_"+arqOut+"t", config.getDirVolume()+"/out/"+arqOut);


        try
        {
            FileUtils.deleteDirectory( new File(config.getDirVolume()+"/out/tmp_"+arqOut+"t")  );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        return res;

    }



}
