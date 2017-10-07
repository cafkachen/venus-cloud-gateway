package org.xujin.venus.cloud.gw.server.netty;

import java.util.concurrent.ExecutionException;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.netty.channel.Channel;

/**
 * 缓存Channel的RemoteAddress与LocalAddress的字符串形式的表达(ip:port).
 * 
 * 基于GuavaCache使用weakKeys, channel失效后会被消除.
 */
public class ChannelAddressCache {

	private static final LoadingCache<Channel, ChannelAddresses> cache = CacheBuilder.newBuilder().concurrencyLevel(32).initialCapacity(128).weakKeys()
			.build(new CacheLoader<Channel, ChannelAddresses>() {
				@Override
				public ChannelAddresses load(Channel channel) throws Exception {
					return getAddressesWithoutCache(channel);
				}
			});
	
	public static void initChannelAddressCache(){
	}

	/**
	 * 如果意外发生，返回的Address对象中Remote/Local对象为空
	 */
	public static ChannelAddresses getAddresses(Channel channel) {
		try {
			return cache.get(channel);
		} catch (ExecutionException e) {// NOSONAR
			return new ChannelAddresses();
		}
	}
	
	public static ChannelAddresses getAddressesWithoutCache(Channel channel) {
		ChannelAddresses addresses = new ChannelAddresses();
		if(channel == null){
			return addresses;
		}
		
		return addresses;
	}

	public static class ChannelAddresses {
		public String localAddress;
		public String remoteAddress;
	}
}
