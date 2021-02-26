package ch.epfl.biop.ij2command;

import bdv.util.BdvFunctions;
import bdv.util.BdvStackSource;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
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

    int index = 1;

    @Override
    public void run() {
        // Run the function
        //ImageStack ipstack = image.getStack();
        // Read image dimensions and set total dimensions of the tiled image accordingly
        long[] total_dim = new long[2];
        total_dim[0] = image.getWidth()*image.getNSlices();
        total_dim[1] = image.getHeight();

        // Create cached image factory of Type Byte
        ReadOnlyCachedCellImgOptions options = new ReadOnlyCachedCellImgOptions();
        // Put cell dimensions to image width and height
        options = options.cellDimensions(image.getWidth(),image.getHeight());
        final ReadOnlyCachedCellImgFactory factory = new ReadOnlyCachedCellImgFactory(options);

        UnsignedShortType t = new UnsignedShortType();

        CellLoader<UnsignedShortType> loader = new CellLoader<UnsignedShortType>(){
            @Override
            public void load(SingleCellArrayImg<UnsignedShortType, ?> singleCellArrayImg) throws Exception {

                ImageProcessor ip = image.getStack().getProcessor(index);

                int[] positions = new int[2];
                Cursor<UnsignedShortType> cursor = singleCellArrayImg.localizingCursor();

                final int cellOffset = - (index-1)*image.getWidth();


                // move through pixels until there is no pixel left in this cell
                while (cursor.hasNext())
                {
                    // move the cursor forward by one pixel
                    cursor.fwd();
                    //get the current position
                    cursor.localize(positions);
                    int px = positions[0] + cellOffset;
                    int py = positions[1];
                    //get pixel value of the input image (from stack) at pos (px,py) and copy it to the current cell at the same position
                    cursor.get().set(ip.getPixel(px,py));
                }
                /*
                singleCellArrayImg.forEach(new Consumer<UnsignedShortType>() {
                    @Override
                    public void accept(UnsignedShortType unsignedShortType) {
                        // move the cursor forward by one pixel
                        cursor.fwd();
                        //get the current position
                        cursor.localize(positions);
                        int px = positions[0] - (index-1)*image.getWidth();
                        int py = positions[1];
                        //get pixel value of the input image (from stack) at pos (px,py) and copy it to the current cell at the same position
                        unsignedShortType.set(ip.getPixel(px,py));
                    }
                });*/
                index = index+1;
            }
        };
        RandomAccessibleInterval<UnsignedShortType> randomAccessible = factory.create(total_dim, t,loader);
        BdvStackSource bss = BdvFunctions.show(randomAccessible,"Tiling");
        bss.setDisplayRange(0, 255);

    }

    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ImagePlus img = IJ.openImage("http://wsr.imagej.net/images/mri-stack.zip");
        img.show();

        //ij.command().run(Tiling.class, true);
    }




}
