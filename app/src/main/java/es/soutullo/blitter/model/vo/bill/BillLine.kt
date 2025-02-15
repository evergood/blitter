package es.soutullo.blitter.model.vo.bill

import android.content.Context
import es.soutullo.blitter.R
import es.soutullo.blitter.model.vo.person.Person
import java.io.Serializable

/** Represents a bill line, with its assigned persons if any */
data class BillLine(val id: Long?, val bill: Bill, val lineNumber: Int, val name: String, val price: Double, val persons: MutableList<Person> = mutableListOf()): Serializable {

    /**
     * Assigns a new person to the bill line. If the person already exists on any line of the bill, then
     * such person is assigned to the bill line. The line is assigned to the person too
     * @param person The person to assign
     */
    fun assignPerson(person: Person) {
        if (!this.persons.contains(person)) {
            val realPerson = this.bill.findPerson(person.name) ?: person

            realPerson.lines.add(this)
            this.persons.add(realPerson)
        }
    }

    /**
     * Unassigns a person from the bill line. The bill line is unassigned from the person too.
     * @param person The person to unassign
     */
    fun unassignPerson(person: Person) {
        this.persons.find{personIt -> personIt.name == person.name}?.let { realPerson ->
            realPerson.lines.remove(this)
            this.persons.remove(realPerson)
        }
    }

    /**
     * Assigns a set of persons to the bill line. If any person already exists on any line of the bill, then
     * such person is assigned to the bill line. The line is assigned to the person too
     * @param persons The persons to assign
     */
    fun assignAllPersons(persons: Collection<Person>) {
        persons.forEach { this.assignPerson(it) }
    }

    /**
     * Unassigns a set of persons from the bill line. The bill line is unassigned from the person too.
     * @param persons The set of persons to unassign
     */
    fun unassignAllPersons(persons: Collection<Person>) {
        persons.forEach { this.unassignPerson(it) }
    }

    /** Unassigns all the persons from this line */
    fun clearAssignations() {
        val personsCopy = ArrayList(this.persons)
        personsCopy.forEach { this.unassignPerson(it) }
    }

    /**
     * Returns the assigned people to this bill line as a string like "A, B" or "A, B, C and 2 more"
     * @param context The android context
     * @return The assigned people as string
     */
    fun getAssignedPeopleAsString(context: Context) : String {
        val separator = context.getString(R.string.persons_array_separator)

        return when(this.persons.isNotEmpty()) {
            true -> this.persons.map { it.name }.reduce { acc, s ->  acc + separator + s}
            false -> context.getString(R.string.item_assignation_persons_unassigned)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BillLine

        if (lineNumber != other.lineNumber) return false

        return true
    }

    override fun hashCode(): Int = lineNumber
}