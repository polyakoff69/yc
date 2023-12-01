package ru.yc.app.mainwnd;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import ru.yc.app.file.FileData;

public class IcoTabCell extends TableCell<FileData, FileData.Info> {
    HBox box;
    ImageView img;
    Label label = new Label("");
    Image[] images = new Image[3];

    public IcoTabCell() {

        images[0] = new Image("folder.gif", 16.0, 16.0, true, true);
        images[1] = new Image("file.gif", 16.0, 16.0, true, true);
        images[2] = new Image("up.gif", 16.0, 16.0, true, true);

        box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(2.0);

        img = new ImageView();
        // img.setFitHeight(100);
        // img.setFitWidth(100);
        img.setVisible(true);
        box.getChildren().addAll(img);

        label.setVisible(true);
        box.getChildren().addAll(label);

        setGraphic(box);

    }//constructor

    @Override
    public void updateItem( FileData.Info fdata, boolean empty ) {
        /* File file = new File(artFile.getArtWork());
        System.out.println("adding the image"+file);
        Image image = new Image(file.toURI().toString());
        art.setImage(image); */
        if(!empty && fdata!=null) {
            Image ico = images[1];
            if(fdata.isFolder())
              ico = images[0];
            if(fdata.isParent())
                ico = images[2];

            img.setImage(ico);
            label.setText(""+fdata);
        }else {
            img.setImage(null);
            label.setText("");
        }
    }

}

