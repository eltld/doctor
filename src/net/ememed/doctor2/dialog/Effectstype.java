package net.ememed.doctor2.dialog;

import net.ememed.doctor2.effect.BaseEffects;
import net.ememed.doctor2.effect.FadeIn;
import net.ememed.doctor2.effect.Fall;
import net.ememed.doctor2.effect.FlipH;
import net.ememed.doctor2.effect.FlipV;
import net.ememed.doctor2.effect.NewsPaper;
import net.ememed.doctor2.effect.RotateLeft;
import net.ememed.doctor2.effect.Shake;
import net.ememed.doctor2.effect.SideFall;
import net.ememed.doctor2.effect.RotateBottom;
import net.ememed.doctor2.effect.SlideBottom;
import net.ememed.doctor2.effect.SlideLeft;
import net.ememed.doctor2.effect.SlideRight;
import net.ememed.doctor2.effect.SlideTop;
import net.ememed.doctor2.effect.Slit;

/**
 * Created by lee on 2014/7/30.
 */
public enum  Effectstype {

    Fadein(FadeIn.class),
    Slideleft(SlideLeft.class),
    Slidetop(SlideTop.class),
    SlideBottom(SlideBottom.class),
    Slideright(SlideRight.class),
    Fall(Fall.class),
    Newspager(NewsPaper.class),
    Fliph(FlipH.class),
    Flipv(FlipV.class),
    RotateBottom(RotateBottom.class),
    RotateLeft(RotateLeft.class),
    Slit(Slit.class),
    Shake(Shake.class),
    Sidefill(SideFall.class);
    private Class effectsClazz;

    private Effectstype(Class mclass) {
        effectsClazz = mclass;
    }

    public BaseEffects getAnimator() {
        try {
            return (BaseEffects) effectsClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}
