package com.pinao.panchitaapp.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CommonTest {

    private val common = Common()

    @Test
    fun `sinAcento replaces all accented vowels with their plain equivalents`() {
        with(common) {
            assertThat("ÁÉÍÓÚáéíóú".sinAcento()).isEqualTo("AEIOUaeiou")
        }
    }

    @Test
    fun `sinAcento returns same string when no accented characters are present`() {
        with(common) {
            assertThat("Hello World 123".sinAcento()).isEqualTo("Hello World 123")
        }
    }

    @Test
    fun `sinAcento handles mixed accented and plain characters`() {
        with(common) {
            assertThat("José García".sinAcento()).isEqualTo("Jose Garcia")
        }
    }

    @Test
    fun `sinAcento handles empty string`() {
        with(common) {
            assertThat("".sinAcento()).isEqualTo("")
        }
    }

    @Test
    fun `sinAcento replaces U umlaut correctly`() {
        with(common) {
            assertThat("Ü ü".sinAcento()).isEqualTo("U u")
        }
    }
}
