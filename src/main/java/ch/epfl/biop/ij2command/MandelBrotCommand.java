package ch.epfl.biop.ij2command;

import bdv.util.BdvStackSource;
import bdv.util.BdvVirtualChannelSource;
import net.imagej.ImageJ;
import net.imglib2.Localizable;
import net.imglib2.view.IntervalView;
import org.scijava.command.Command;
import org.scijava.platform.PlatformService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;


import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import java.util.List;
import java.util.function.BiConsumer;
import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;
import net.imglib2.FinalInterval;
import net.imglib2.view.Views;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;

import java.io.IOException;




import ij.*;
import ij.IJ;


import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;

/**
 * This example illustrates how to create an ImageJ 2 {@link Command} plugin.
 * The pom file of this project is customized for the PTBIOP Organization (biop.epfl.ch)
 * <p>
 * The code here is opening the biop website. The command can be tested in the java DummyCommandTest class.
 * </p>
 */

@Plugin(type = Command.class, menuPath = "Plugins>BIOP>Dummy Command")
public class MandelBrotCommand implements Command {

    @Parameter
    UIService uiService;

    @Parameter
    PlatformService ps;




    @Override
    public void run() {
        // Run the function
            System.out.println( "hello" );
            BiConsumer b;

        FunctionRandomAccessible randomAccessible;
        randomAccessible = new FunctionRandomAccessible<UnsignedShortType>(3,
        this::mandelbrot,this::supplypixel);


        IntervalView  rai = Views.interval(randomAccessible, FinalInterval.createMinMax( 0, 0, 0, 23, 23, 23));

        AffineTransform3D at3Didentity = new AffineTransform3D();
        AffineTransform3D at3Dsheared = new AffineTransform3D();

        at3Dsheared.set(0.5,0,1);
        at3Dsheared.translate(24,0,0);

        BdvStackSource bss = BdvFunctions.show(rai,"Image",
        BdvOptions.options()
        .sourceTransform(at3Didentity) // AffineTransform used for the rai
    );

        bss.setDisplayRange( 0, 3 );

        bss.setColor(new ARGBType(ARGBType.rgba(0,255,0,1)));

        bss = BdvFunctions.show(rai,"Sheared Image",
            BdvOptions.options()
            .sourceTransform(at3Dsheared) // AffineTransform used for the rai
		.addTo(bss.getBdvHandle()) // Appends data to an existing Bdv window -> reference taken from the first displayed image
            );
        bss.setDisplayRange( 0, 3 );
        bss.setColor(new ARGBType(ARGBType.rgba(255,0,0,1)));

    }

    public void mandelbrot(Localizable l, UnsignedShortType t){
            int px = l.getIntPosition(0);
            int py = l.getIntPosition(1);
            int pz = l.getIntPosition(2);
            t.set((int) ((px/4)%2 + (int)(py/4)%2 + (int)(pz/4)%2)); // Checkerboard
            t.set(0);
    }

    public UnsignedShortType supplypixel(){
        return new UnsignedShortType();
    }




    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();

        ij.command().run(MandelBrotCommand.class, true);
    }

}