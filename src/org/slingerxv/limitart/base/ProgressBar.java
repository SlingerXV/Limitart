/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.base;

import org.slingerxv.limitart.util.GameMathUtil;

/**
 * 进度条
 *
 * @author hank
 * @version 2018-02-26
 */
@ThreadUnsafe
public class ProgressBar extends IntCounter {
    private final int high;

    public ProgressBar(int high) {
        this.high = GameMathUtil.fixedBetween(high, low(), super.high());
    }

    @Override
    protected int high() {
        return this.high;
    }
}
