package com.g2forge.gearbox.git;

public interface IGitConfigAccessor {
	/**
	 * Save all changes in the {@link GitConfig} from which this accessor derives. Note that this may save more changes than those made through the object on
	 * which it is called.
	 */
	public void save();
}
