package tr.xip.scd.rulebook.model

import java.io.Serializable

class Rule(val id: Int, val bosnian: String, val english: String, val result: String, val note_bosnian: String, val note_english: String) : Serializable