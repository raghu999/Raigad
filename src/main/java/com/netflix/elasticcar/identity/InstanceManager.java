package com.netflix.elasticcar.identity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.elasticcar.IConfiguration;
import com.netflix.elasticcar.utils.RetryableCallable;

/**
 * This class provides the central place to create and consume the identity of
 * the instance
 * 
 */
@Singleton
public class InstanceManager {

	private static final Logger logger = LoggerFactory
			.getLogger(InstanceManager.class);
	private final IElasticCarInstanceFactory instanceFactory;
	private final IConfiguration config;
	private ElasticCarInstance myInstance;
	private List<ElasticCarInstance> instanceList;

	@Inject
	public InstanceManager(IElasticCarInstanceFactory instanceFactory,
			IConfiguration config) throws Exception {

		this.instanceFactory = instanceFactory;
		this.config = config;
		init();
	}

	private void init() throws Exception {
		logger.info("***Registering Instance");
		myInstance = new RetryableCallable<ElasticCarInstance>() 
		{
			@Override
			public ElasticCarInstance retriableCall() throws Exception 
			{
				ElasticCarInstance instance = registerInstance(instanceFactory,config);
				return instance;
			}
		}.call();
		logger.info("ElasticCarInstance Details = "+myInstance.toString());
	}

	private ElasticCarInstance registerInstance(
			IElasticCarInstanceFactory instanceFactory, IConfiguration config) throws Exception {
		return instanceFactory
				.create(config.getAppName(),
						config.getDC() + "." + config.getInstanceId(),
						config.getInstanceId(), config.getHostname(),
						config.getHostIP(), config.getRac(), null);
	}

	public ElasticCarInstance getInstance()
	{
		return myInstance;
	}
}