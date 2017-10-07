package org.xujin.venus.cloud.gw.server.config.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xujin.venus.cloud.gw.server.config.vo.PublishDataVO;
import org.xujin.venus.cloud.gw.server.config.vo.PublishOperation;
import org.xujin.venus.cloud.gw.server.config.vo.PublishType;

/**
 * 
 * @author xujin
 *
 */
@SuppressWarnings("rawtypes")
public abstract class Observable {

	protected Vector<AbstactFilterObserver> obs = new Vector<AbstactFilterObserver>();
	public Map<String, Class> typeMap = new HashMap<String, Class>();

	public synchronized void addObserver(AbstactFilterObserver o) {
		if (o == null)
			throw new NullPointerException();
		if (!obs.contains(o)) {
			typeMap.put(o.getPublishType().getName(), o.getPublishType().getClazz());
			obs.addElement(o);
		}
	}

	public synchronized void deleteObserver(AbstactFilterObserver o) {
		obs.removeElement(o);
		typeMap.remove(o.getPublishType().getName());
	}

	/**
	 * 按照 C,R,U,D更新数据
	 * @param vo
	 */
	@SuppressWarnings("unchecked")
	public synchronized void notifyObserver(PublishDataVO vo) {
		for (AbstactFilterObserver abserver : obs) {
			PublishType publishType = abserver.getPublishType();
			if (publishType != null && publishType.getName().equals(vo.getType())) {
				if (PublishOperation.add.equals(vo.getOperation())) {
					abserver.add(vo.getResourceId(), vo.getData());
				}
				else if (PublishOperation.del.equals(vo.getOperation())) {
					abserver.delete(vo.getResourceId(), vo.getData());
				}
				else if (PublishOperation.update.equals(vo.getOperation())) {
					abserver.update(vo.getResourceId(), vo.getData());
				}
				else {
					throw new IllegalArgumentException(vo.getType() + " illegal");
				}

			}
		}
	}

	// 一次触发的开始
	public synchronized void notifyStart(String name) {
		for (AbstactFilterObserver abserver : obs) {
			if (abserver.getPublishType().getName().equals(name)) {
				abserver.start();
			}
		}
	}

	// 一次触发的结束
	public synchronized void notifyComplete(String name) {
		for (AbstactFilterObserver abserver : obs) {
			if (abserver.getPublishType().getName().equals(name)) {
				abserver.complete();
			}
		}
	}

}
