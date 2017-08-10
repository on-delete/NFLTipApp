package com.andre.nfltipapp.loginregistryview;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;

public class ResetPasswordSuccessFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password_success,container,false);

        String email = this.getArguments().getString("email");

        initView(view, email);
        return view;
    }

    private void initView(View view, String email){
        TextView tvLoginLink = (TextView) view.findViewById(R.id.text_login);
        TextView tvSuccessText = (TextView) view.findViewById(R.id.text_password_reset_successfull);

        String successString = String.format("%s %s %s", Constants.SUCCESS_STRING_PART_1,
                email, Constants.SUCCESS_STRING_PART_2);
        tvSuccessText.setText(successString);

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }


    private void goToLogin(){
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_host, login);
        ft.commit();
    }
}
