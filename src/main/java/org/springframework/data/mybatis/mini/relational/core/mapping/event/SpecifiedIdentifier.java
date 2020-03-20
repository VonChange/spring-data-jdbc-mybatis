/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mybatis.mini.relational.core.mapping.event;

import java.util.Optional;

/**
 * Simple value object for {@link Identifier.Specified}.
 *
 * @author Jens Schauder
 * @author Oliver Gierke
 */
class SpecifiedIdentifier implements Identifier.Specified {

	Object value;
	public static   SpecifiedIdentifier of(Object value) {
		return new SpecifiedIdentifier(value);
	}

	private SpecifiedIdentifier(Object value) {
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jdbc.core.mapping.event.Identifier#getOptionalValue()
	 */
	@Override
	public Optional<?> getOptionalValue() {
		return Optional.of(value);
	}
}
