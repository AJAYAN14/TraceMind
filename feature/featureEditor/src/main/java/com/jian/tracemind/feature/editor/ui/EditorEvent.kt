/*
 *
 *  *  Copyright (c) 2026 Dhanush Sugganahalli <dhanush41230@gmail.com>
 *  *
 *  *  This program is free software; you can redistribute it and/or modify it under
 *  *  the terms of the GNU General Public License as published by the Free Software
 *  *  Foundation; either version 3 of the License, or (at your option) any later
 *  *  version.
 *  *
 *  *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License along with
 *  *  this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.jian.tracemind.feature.editor.ui

import androidx.compose.ui.focus.FocusState

sealed class EditorEvent {
    data class EnteredTitle(
        val value: String,
    ) : EditorEvent()

    data class EnteredContent(
        val value: String,
    ) : EditorEvent()

    data class ChangeTitleFocus(
        val focusState: FocusState,
    ) : EditorEvent()

    data class ChangeContentFocus(
        val focusState: FocusState,
    ) : EditorEvent()

    data class ChangeColor(
        val color: Int,
    ) : EditorEvent()

    data class InsertImage(
        val uriString: String,
    ) : EditorEvent()

    object SaveNote : EditorEvent()

    data class SetReminder(
        val timestamp: Long?,
    ) : EditorEvent()

    data class SetMood(val mood: String?) : EditorEvent()
    
    data class SetWeather(val weather: String?) : EditorEvent()
    
    data class SetTags(val tags: List<String>) : EditorEvent()

    data class SetLocation(val location: String?) : EditorEvent()
}
