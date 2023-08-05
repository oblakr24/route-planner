package com.rokoblak.routeplanner.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface TextRes {

    data class Text(val text: String) : TextRes {

        @Composable
        override fun resolve(): String = text
    }

    data class Res(@StringRes val id: Int, val args: List<Any> = emptyList()) : TextRes {

        @Composable
        override fun resolve(): String {
            return if (args.isNotEmpty()) {
                stringResource(id = id, *args.toTypedArray())
            } else {
                stringResource(id = id)
            }
        }

        companion object {
            fun create(@StringRes id: Int, vararg formatArgs: Any?): Res {
                return Res(id, formatArgs.filterNotNull().toList())
            }
        }
    }

    @Composable
    fun resolve(): String
}
