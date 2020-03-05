package com.shawn.researjour.Models;

public class CategoryModel {
    private boolean isChecked = false;
    public int imagePositionNoSelection;
    public int imagePositionSelection;
    public String textPosition;



    public CategoryModel(){

    }

    public CategoryModel(String textPosition, int imagePositionNoSelection, int imagePositionSelection){

        this.textPosition = textPosition;
        this.imagePositionNoSelection = imagePositionNoSelection;
        this.imagePositionSelection = imagePositionSelection;

    }
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getImagePositionNoSelection() {
        return imagePositionNoSelection;
    }

    public int getImagePositionSelection() {
        return imagePositionSelection;
    }

    public void setImagePositionNoSelection(int imagePositionNoSelection) {
        this.imagePositionNoSelection = imagePositionNoSelection;
    }

    public void setImagePositionSelection(int imagePositionSelection) {
        this.imagePositionSelection = imagePositionSelection;
    }

    public String getTextPosition() {
        return textPosition;
    }



    public void setTextPosition(String textPosition) {
        this.textPosition = textPosition;
    }
}
