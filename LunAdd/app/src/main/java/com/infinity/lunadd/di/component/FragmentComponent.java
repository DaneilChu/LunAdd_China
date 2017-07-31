package com.infinity.lunadd.di.component;

import com.infinity.lunadd.di.scope.FragmentScope;
import com.infinity.lunadd.mvp.view.home.fragment.LunAddFragment;
import com.infinity.lunadd.mvp.view.home.fragment.ProfileFragment;
import com.infinity.lunadd.mvp.view.home.fragment.WallFragment;

import dagger.Component;

/**
  * Created by DanielChu on 2017/6/7.
  * Fragment容器，依賴於ActivityComponent
  * 負責為註入的Fragment提供對象，限定對應的對象的生命週期
  */
@FragmentScope
@Component(dependencies = ActivityComponent.class)
public interface FragmentComponent {

    void inject(LunAddFragment fragment);

    void inject(ProfileFragment fragment);

    void inject(WallFragment fragment);

}

