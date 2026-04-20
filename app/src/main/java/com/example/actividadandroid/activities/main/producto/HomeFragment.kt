package com.example.actividadandroid.activities.main.producto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.actividadandroid.R



/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
        private val listaProductos = listOf(
            Product("Chaqueta pára Dama 1", 10.99, R.drawable.chaquta1),
            Product("Chaqueta pára Dama 2", 100.99, R.drawable.chaquta2),
            Product("Chaqueta pára Dama 3", 210.99, R.drawable.chaquta3),
            Product("Chaqueta pára Dama 4", 130.99, R.drawable.chaquta4),

        )


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_home, container, false)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_productos)
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.adapter = ProductoAdapter(listaProductos)
            return view


        }
}
