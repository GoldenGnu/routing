/*
 * Copyright 2015-2020, Niklas Kyster Rasmussen, Flaming Candle
 *
 * This file is part of Routing
 *
 * Routing is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Routing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Routing; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package uk.me.candle.eve.routing;

/**
 *
 * @author Candle
 */
public interface Progress {
    public int getMaximum();
    public void setMaximum(int maximum);
    public int getMinimum();
    public void setMinimum(int minimum);
    public int getValue();
    public void setValue(int value);
}
