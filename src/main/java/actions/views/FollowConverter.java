package actions.views;

import java.util.ArrayList;
import java.util.List;

import models.Follow;

public class FollowConverter {

    public static Follow toModel(FollowView fv) {
        return new Follow(
                fv.getId(),
                fv.getFollow(),
                fv.getFollower(),
                fv.getFollowDay()
                );
    }

    public static FollowView toView(Follow f) {
        if(f == null) {
            return null;
        }else {
            return new FollowView(
                    f.getId(),
                    f.getFollow(),
                    f.getFollower(),
                    f.getFollowDay()
                    );
        }
    }

    public static List<FollowView> toList(List<Follow> list){
        List<FollowView> fvl = new ArrayList<>();
        for(Follow f :list) {
            fvl.add(toView(f));
        }
        return fvl;
    }


}
