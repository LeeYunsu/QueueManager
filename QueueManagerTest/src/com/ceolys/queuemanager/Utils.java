package com.ceolys.queuemanager;

public class Utils {
	public static boolean equlsFlag(int unifyFlag, int flag){
		return (unifyFlag & flag) == flag;
	}
}
