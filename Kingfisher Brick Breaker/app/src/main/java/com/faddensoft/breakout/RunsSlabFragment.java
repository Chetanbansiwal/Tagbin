package com.faddensoft.breakout;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RunsSlabFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.runs_slab_fragment, container, false);
        final Button practise = (Button) view.findViewById(R.id.practise);
        final Button playGame = (Button) view.findViewById(R.id.playGame);
        practise.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                ButtonClicked(v);
            }

        });
        playGame.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                ButtonClicked(v);
            }

        });
        return view;
    }

    public void ButtonClicked(View v)
    {
        Fragment fr = new TermsFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment, fr).commit();
    }
}

