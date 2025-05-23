package core.process.lyrics

import core.model.Note
import core.model.Project
import core.model.Track
import core.process.validateNotes
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LyricsMappingRequest(
    val mapText: String = "",
    val mapToPhonemes: Boolean = false,
) {
    val isValid get() = map.isNotEmpty()

    @Transient
    val map =
        mapText
            .lines()
            .mapNotNull { line ->
                if (line.contains("=").not()) return@mapNotNull null
                val from = line.substringBefore("=").trim()
                val to = line.substringAfter("=").trim()
                from to to
            }.toMap()

    companion object {
        fun findPreset(name: String) = Presets.find { it.first == name }?.second

        fun getPreset(name: String) = requireNotNull(findPreset(name))

        val Presets: List<Pair<String, LyricsMappingRequest>> by lazy {
            listOf(
                "VX-β 日本語かな変換" to
                    LyricsMappingRequest(
                        mapText = core.external.Resources.lyricsMappingVxBetaJaText,
                        mapToPhonemes = false,
                    ),
            )
        }
    }
}

fun Project.mapLyrics(request: LyricsMappingRequest) =
    copy(
        tracks = tracks.map { it.replaceLyrics(request) },
    )

private fun Track.replaceLyrics(request: LyricsMappingRequest) =
    copy(
        notes =
            notes
                .mapNotNull { note -> note.replaceLyrics(request).takeIf { it.lyric.isNotEmpty() } }
                .validateNotes(),
    )

private fun Note.replaceLyrics(request: LyricsMappingRequest): Note {
    val mappedValue = request.map[this.lyric] ?: this.lyric
    return if (request.mapToPhonemes) {
        this.copy(phoneme = mappedValue)
    } else {
        this.copy(lyric = mappedValue)
    }
}
