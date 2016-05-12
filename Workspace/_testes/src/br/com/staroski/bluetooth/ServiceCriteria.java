package br.com.staroski.bluetooth;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;

public class ServiceCriteria {

	public static ServiceCriteria create(int attribute, UUID uuid, RemoteDevice device) {
		return new ServiceCriteria(new int[] { attribute }, new UUID[] { uuid }, device);
	}

	private ServiceCriteria(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev) {
		super();
		this.attrSet = attrSet;
		this.uuidSet = uuidSet;
		this.btDev = btDev;
	}

	private final int[] attrSet;
	private final UUID[] uuidSet;
	private final RemoteDevice btDev;

	public int[] attributes() {
		return attrSet;
	}

	public UUID[] uuids() {
		return uuidSet;
	}

	public RemoteDevice device() {
		return btDev;
	}
}
