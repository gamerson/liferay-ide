/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.core.event;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Charles Wu
 */
public final class DefaultListenerRegistry implements LiferayListenerRegistry {

	@Override
	public void addEventListener(EventListener listener) {
		synchronized (_lock) {
			_listeners.add(listener);
		}
	}

	@Override
	public void dispatch(ProjectsChangeEvent event) {
		Collection<EventListener> listeners;
		synchronized (_lock) {
			listeners = Collections.unmodifiableCollection(_listeners);
		}

		for (EventListener listener : listeners) {
			listener.onEvent(event);
		}
	}

	@Override
	public void removeEventListener(EventListener listener) {
		synchronized (_lock) {
			_listeners.remove(listener);
		}
	}

	private final Set<EventListener> _listeners = new LinkedHashSet<>();
	private final Object _lock = new Object();

}