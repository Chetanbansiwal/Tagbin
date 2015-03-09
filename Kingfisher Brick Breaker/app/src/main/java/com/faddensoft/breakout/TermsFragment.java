package com.faddensoft.breakout;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TermsFragment extends Fragment {
    private PlayNewGame newGame;
    public interface PlayNewGame
    {
        void NewGame();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.terms_fragment, container, false);
        final Button playGame = (Button) view.findViewById(R.id.AgreeButton);
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
        ((BreakoutActivity)getActivity()).clickNewGame(v);
       // newGame.NewGame();
      //  Fragment fr = new ThanksFragment();
      //  getFragmentManager().beginTransaction().replace(R.id.fragment, fr).commit();
    }
}

