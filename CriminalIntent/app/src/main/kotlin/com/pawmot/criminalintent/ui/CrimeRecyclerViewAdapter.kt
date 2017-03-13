package com.pawmot.criminalintent.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawmot.criminalintent.CrimePagerActivity
import com.pawmot.criminalintent.R
import com.pawmot.criminalintent.model.Crime
import kotlinx.android.synthetic.main.list_item_crime.view.*

class CrimeRecyclerViewAdapter(private var crimes: List<Crime>) : RecyclerView.Adapter<CrimeRecyclerViewAdapter.CrimeHolder>() {

    class CrimeHolder(view: View, private val ctx: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private lateinit var crime: Crime

        fun bindCrime(crime: Crime) {
            itemView.listItemCrimeSolvedCheckBox.setOnCheckedChangeListener(null)
            this.crime = crime
            itemView.listItemCrimeTitleTextView.text = crime.title
            itemView.listItemCrimeDateTextView.text = crime.date.toString()
            itemView.listItemCrimeSolvedCheckBox.isChecked = crime.solved
            itemView.listItemCrimeSolvedCheckBox.setOnCheckedChangeListener { compoundButton, checked ->
                crime.solved = checked
            }
        }

        override fun onClick(v: View?) {
            val intent = CrimePagerActivity.newIntent(ctx, crime.uuid!!)
            ctx.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CrimeHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.list_item_crime, parent, false)
        return CrimeHolder(view, parent?.context!!)
    }

    override fun onBindViewHolder(holder: CrimeHolder?, position: Int) {
        holder?.bindCrime(crimes[position])
    }

    override fun getItemCount(): Int {
        return crimes.size
    }

    fun setCrimes(crimes: List<Crime>) {
        this.crimes = crimes
    }
}