package com.pawmot.beatbox

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_beat_box.*
import kotlinx.android.synthetic.main.list_item_sound.view.*

class BeatBoxFragment : Fragment() {
    companion object Static {
        fun newInstance(): BeatBoxFragment {
            return BeatBoxFragment()
        }
    }

    private lateinit var beatBox: BeatBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        beatBox = BeatBox(activity)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_beat_box, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        fragmentBeatBoxRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        fragmentBeatBoxRecyclerView.adapter = SoundAdapter(beatBox.sounds)
    }

    override fun onDestroy() {
        super.onDestroy()
        beatBox.release()
    }

    inner class SoundHolder(inflater: LayoutInflater, container: ViewGroup?) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_sound, container, false)),
            View.OnClickListener {
        private val button = itemView.listItemSoundButton
        private var sound: Sound? = null

        init {
            button.setOnClickListener(this)
        }

        fun bindSound(sound: Sound) {
            this.sound = sound
            button.text = sound.name
        }

        override fun onClick(v: View?) {
            beatBox.play(sound!!)
        }
    }

    inner class SoundAdapter(private val sounds: List<Sound>) : RecyclerView.Adapter<SoundHolder>() {
        override fun getItemCount(): Int {
            return sounds.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SoundHolder {
            val inflater = LayoutInflater.from(this@BeatBoxFragment.activity)
            return SoundHolder(inflater, parent)
        }

        override fun onBindViewHolder(holder: SoundHolder?, position: Int) {
            val sound = sounds[position]
            holder?.bindSound(sound)
        }

    }
}