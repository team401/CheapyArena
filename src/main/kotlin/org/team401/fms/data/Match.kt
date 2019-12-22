package org.team401.fms.data

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty

/**
 * Data class for a match.  Contains all information about the match, including teams, states, played status, etc.
 *
 * Does NOT manage active match play details, such as the match time, game state, etc.
 */
class Match(blue1: Int, blue2: Int, blue3: Int, red1: Int, red2: Int, red3: Int, played: Boolean = false) {
    private val blue1 = SimpleIntegerProperty(blue1)
    private val blue2 = SimpleIntegerProperty(blue2)
    private val blue3 = SimpleIntegerProperty(blue3)
    private val red1 = SimpleIntegerProperty(red1)
    private val red2 = SimpleIntegerProperty(red2)
    private val red3 = SimpleIntegerProperty(red3)

    private val played = SimpleBooleanProperty(played)

    /**
     * Creates a clone copy of this match that is not coupled to the old data
     */
    fun clone(): Match {
        return Match(blue1.get(), blue2.get(), blue3.get(), red1.get(), red2.get(), red3.get(), played.get())
    }
    
    fun getBluePlayingTeamsAsList(): List<Int> {
        val list = arrayListOf<Int>()
        if (blue1.get() != 0) list.add(blue1.get())
        if (blue2.get() != 0) list.add(blue2.get())
        if (blue3.get() != 0) list.add(blue3.get())
        return list
    }

    fun getRedPlayingTeamsAsList(): List<Int> {
        val list = arrayListOf<Int>()
        if (red1.get() != 0) list.add(red1.get())
        if (red2.get() != 0) list.add(red2.get())
        if (red3.get() != 0) list.add(red3.get())
        return list
    }

    //Accessors required by JavaFX

    fun getBlue1() = blue1.get()
    fun getBlue2() = blue2.get()
    fun getBlue3() = blue3.get()
    fun getRed1() = red1.get()
    fun getRed2() = red2.get()
    fun getRed3() = red3.get()
    fun setBlue1(value: Int) = blue1.set(value)
    fun setBlue2(value: Int) = blue2.set(value)
    fun setBlue3(value: Int) = blue3.set(value)
    fun setRed1(value: Int) = red1.set(value)
    fun setRed2(value: Int) = red2.set(value)
    fun setRed3(value: Int) = red3.set(value)
    fun blue1Property() = blue1
    fun blue2Property() = blue2
    fun blue3Property() = blue3
    fun red1Property() = red1
    fun red2Property() = red2
    fun red3Property() = red3

    fun getPlayed() = played.get()
    fun setPlayed(value: Boolean) = played.set(value)
    fun playedProperty() = played
}