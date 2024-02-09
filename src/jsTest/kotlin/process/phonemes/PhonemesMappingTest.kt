package process.phonemes

import model.Note
import kotlin.test.Test
import kotlin.test.assertEquals

class PhonemesMappingTest {

    private val request = PhonemesMappingRequest(
        mapText = """
            a=A
            b=B
            c a=C' A
            c=C
            d c a=DC' A
            d c=DC
        """.trimIndent(),
    )

    private fun createNote(phoneme: String) = Note(
        id = 0,
        key = 60,
        lyric = "",
        tickOn = 0L,
        tickOff = 480L,
        phoneme = phoneme,
    )

    @Test
    fun testNoMatch() {
        val note = createNote("l e")
        val actual = note.replacePhonemes(request).phoneme
        assertEquals("l e", actual)
    }

    @Test
    fun testSingleMatch() {
        val note = createNote("b")
        val actual = note.replacePhonemes(request).phoneme
        assertEquals("B", actual)
    }

    @Test
    fun testSingleInMultipleMatch() {
        val note = createNote("l a m b n")
        val actual = note.replacePhonemes(request).phoneme
        assertEquals("l A m B n", actual)
    }

    @Test
    fun testMultipleMatch() {
        val note = createNote("c a")
        val actual = note.replacePhonemes(request).phoneme
        assertEquals("C' A", actual)
    }

    @Test
    fun testMultipleInMultipleMatch() {
        val note = createNote("d c a m d c")
        val actual = note.replacePhonemes(request).phoneme
        assertEquals("DC' A m DC", actual)
    }
}
