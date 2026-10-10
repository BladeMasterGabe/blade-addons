package blade.addon.features.item;

public interface StarCountHolder {
    boolean blade_addons$hasScannedStars();

    void blade_addons$setStarCount(int count);

    int blade_addons$getStarCount();
}