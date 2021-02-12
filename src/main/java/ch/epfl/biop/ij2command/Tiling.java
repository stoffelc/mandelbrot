package ch.epfl.biop.ij2command;

import bdv.util.BdvFunctions;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.cache.img.CellLoader;
import net.imglib2.cache.img.ReadOnlyCachedCellImgFactory;
import net.imglib2.cache.img.ReadOnlyCachedCellImgOptions;
import net.imglib2.cache.img.SingleCellArrayImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.function.Consumer;

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Tiling")
public class Tiling implements Command {
/*
    @Parameter
    int nb_col = 50;

    @Parameter
    int nb_row = 50;
*/
    @Parameter
    ImagePlus image;

    int index = 0;

    @Override
    public void run() {
        ImageProcessor ip = image.getStack().getProcessor(1);
        image.getHeight();
        image.getWidth();
        image.getNSlices();

        // Run the function
        // Creates cached image factory of Type Byte
        ReadOnlyCachedCellImgOptions options = new ReadOnlyCachedCellImgOptions();
        options = options.cellDimensions(25,10);
        final ReadOnlyCachedCellImgFactory factory = new ReadOnlyCachedCellImgFactory(options);
        long[] dim = new long[2];
        dim[0] = 1024;
        dim[1] = 1024;
        UnsignedShortType t = new UnsignedShortType();
        CellLoader<UnsignedShortType> loader = new CellLoader<UnsignedShortType>(){
            @Override
            public void load(SingleCellArrayImg<UnsignedShortType, ?> singleCellArrayImg) throws Exception {
                index = index+10;
                int index1 = index;
                int[] positions = new int[2];

                Cursor<UnsignedShortType> cursor = singleCellArrayImg.localizingCursor();
                while (cursor.hasNext())
                {
                    // move both cursors forward by one pixel
                    cursor.fwd();
                    cursor.localize(positions);
                    int px = positions[0];
                    int py = positions[1];
                    // set the value of this pixel of the output image to the same as the input,
                    // every Type supports T.set( T type )
                    //cursor.get().set(px*py);
                    cursor.get().set(ip.getPixel(px,py));
                }
                /*singleCellArrayImg.forEach(new Consumer<UnsignedShortType>() {
                    @Override
                    public void accept(UnsignedShortType unsignedShortType) {
                        //unsignedShortType.set((int) (Math.random()*255));
                        unsignedShortType.set(px*py);
                    }
                });*/
            }
        };
        RandomAccessibleInterval<UnsignedShortType> randomAccessible = factory.create(dim, t,loader);
        BdvFunctions.show(randomAccessible,"Tiling");
    }

    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ImagePlus img = IJ.openImage("http://wsr.imagej.net/images/mri-stack.zip");
        img.show();

        ij.command().run(Tiling.class, true);
    }




}
