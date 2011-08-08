/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.image;

import java.io.Serializable;
import java.util.Comparator;

import org.openimaj.image.processor.GridProcessor;
import org.openimaj.image.processor.ImageProcessor;
import org.openimaj.image.processor.KernelProcessor;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.image.renderer.RenderHints;
import org.openimaj.image.renderer.ImageRenderer;
import org.openimaj.math.geometry.shape.Rectangle;

import Jama.Matrix;

/**
 * Base class for representing and manipulating images.  Images are typed
 * by the type of pixel at each coordinate and the concrete subclass type. 
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 *
 * @param <Q> the pixel type
 * @param <I> the actual image of the concrete subclass
 */
public abstract class Image<Q, I extends Image<Q, I>> implements Cloneable, Serializable {
	/**
	 *	Enumerator for representing the type of field 
	 *	interlacing operations.	
	 *	@author Jonathon Hare <jsh2@ecs.soton.ac.uk>
	 */
	public enum Field 
	{
		/**
		 * Odd field
		 */
		ODD,
		/**
		 * Even field
		 */
		EVEN
	}

	private static final long serialVersionUID = 1L;
	
	/**
	 * Set all pixels to their absolute values, so that all
	 * pixel values in the image will be greater than zero.
	 * 
	 * @return The image with absolute values
	 */
	public abstract I abs();
	
	
	
	/**
	 * Adds the given image to this image and return new image.
	 * 
	 * @param im The image to add 
	 * @return A new image that is the sum of this image and the given image.
	 */
	public I add(Image<?,?> im) {
		I newImage = this.clone();
		newImage.addInline(im);
		return newImage;
	}
	
	/**
	 * Add a value to each pixel and return new image.
	 * 
	 * @param num The value to add to each pixel
	 * @return A new image that is the sum of this image and the given value.
	 */
	public I add(Q num) {
		I newImage = this.clone();
		newImage.addInline(num);
		return newImage;
	}

	/**
	 * Add the given image to this image (side-affects this image).
	 * 
	 * @param im The image to add to this image 
	 * @return A reference to this image.
	 */
	public abstract I addInline(Image<?,?> im);
	
	/**
	 * Add a scalar to each pixel in this image (side-affects this image).
	 * 
	 * @param num The value to add to every pixel in this image.
	 * @return A reference to this image.
	 */
	public abstract I addInline(Q num);
	
	/**
	 * Sets any pixels that are below <code>min</code> or above <code>max</code>
	 * to zero. This method may side-affect this image.
	 * 
	 * @param min The minimum value
	 * @param max The maximum value
	 * @return The clipped image.
	 */
	public abstract I clip(Q min, Q max);

	/**
	 * Set all values greater than the given value to zero. This method
	 * may side-affect this image.
	 * 
	 * @param thresh The value over which pixels are clipped to zero.
	 * @return The clipped image.
	 */
	public abstract I clipMax(Q thresh);
	
	/**
	 * Set all values less than the given value to zero. This method may
	 * side-affect this image.
	 * 
	 * @param thresh The value below which pixels are clipped to zero.
	 * @return The clipped image.
	 */
	public abstract I clipMin(Q thresh);
	
	/**
	 * Deep copy of an image (internal image buffers copied).
	 * @return A copy of this image.
	 */
	@Override
	public abstract I clone();

	/**
	 * Create a {@link ImageRenderer} capable of drawing into
	 * this image.
	 * 
	 * @return the renderer
	 */
	public abstract ImageRenderer<Q,I> createRenderer();

	/**
	 * Create a {@link ImageRenderer} capable of drawing into
	 * this image.
	 * 
	 * @param options Options for the renderer
	 * @return the renderer
	 */
	public abstract ImageRenderer<Q,I> createRenderer(RenderHints options);
	
	/**
	 * Divide each pixel of the image by corresponding pixel in the given
	 * image. This method should return a new image.
	 * 
	 * @param im image The image to divide this image by.
	 * @return A new image containing the result.
	 */
	public I divide(Image<?,?> im) {
		I newImage = this.clone();
		newImage.divideInline(im);
		return newImage;
	}

	/**
	 * Divide each pixel of the image by the given scalar value. This method
	 * should return a new image.  
	 * 
	 * @param val The value to divide the pixels in this image by.
	 * @return A new image containing the result.
	 */
	public I divide(Q val) {
		I newImage = this.clone();
		newImage.divideInline(val);
		return newImage;
	}
	
	/**
	 * Divide each pixel in this image by the corresponding pixel value in
	 * the given image. This method should side-affect this image.
	 *   
	 * @param im image The image to divide this image by.
	 * @return A reference to this image containing the result.
	 */
	public abstract I divideInline(Image<?,?> im);
	
	/**
	 * Divide each pixel of the image by the given scalar value. This method
	 * should side-affect this image. 
	 * 
	 * @param val The value to divide each pixel by.
	 * @return A reference to this image containing the result.
	 */
	public abstract I divideInline(Q val);
	
	/**
	 * Extract a rectangular region about the centre of the image with
	 * the given width and height. The method will return a box that
	 * extends <code>width/2</code> and <code>height/2</code> from the
	 * centre point so that the centre point of the extracted box is
	 * also the centre point of the image.
	 * 
	 * @param w The width of the box to extract
	 * @param h The height of the box to extract 
	 * @return A new image centred around the centre of the image.
	 */
	public I extractCenter(int w, int h){
		int selectedX = (int) Math.floor(this.getWidth() / 2.0);
		int selectedY = (int) Math.floor(this.getHeight() / 2.0);
		
		return this.extractCenter(selectedX, selectedY, w, h);
	}
	
	/**
	 * Extract a rectangular region centred on a given point. The method
	 * will return a box that extends <code>width/2</code> and <code>height/2</code>
	 * from the given point <code>(x,y)</code> such that the centre point of
	 * the extracted box is the same as the point <code>(x,y)</code> in this
	 * image.
	 * 
	 * @param x Center point of the rectangle to extract
	 * @param y center point of the rectangle to extract
	 * @param w The width of the rectangle to extract
	 * @param h The height of the rectangle to extract
	 * @return A new image centred around the centre of the image.
	 */
	public I extractCenter(int x, int y, int w, int h){
		if(w % 2 == 0 ) w+=1;
		if(h % 2 == 0 ) h+=1;
		
		int roiX = Math.max(0,x-(int)(w/2.0));
		int roiY = Math.max(0,y-(int)(h/2.0));
		
		int newWidth = (int)(w / 2.0) + 1 + (x - roiX);
		int newHeight = (int)(h / 2.0) + 1 + (y - roiY);
		
		int roiW = roiX + newWidth > this.getWidth() ? this.getWidth() - roiX : newWidth;
		int roiH = roiY + newHeight > this.getHeight() ? this.getHeight() - roiY : newHeight;
		
		return this.extractROI(roiX, roiY, roiW, roiH);
	}

	/**
	 * Extract a rectangular region of interest from this image and put
	 * it in the given image. Coordinate <code>(0,0)</code> is the top-left corner.
	 * The width and height of the extracted image should be determined
	 * from the given image's width and height. Returns a new image.
	 * 
	 * @param x The leftmost coordinate of the rectangle to extract 
	 * @param y The topmost coordinate of the rectangle to extract
	 * @param img The destination image
	 * @return A reference to the destination image containing the result
	 */
	public abstract I extractROI(int x, int y, I img);

	/**
	 * Extract a rectangular region of interest of the given width and height.
	 * Coordinate <code>(0,0)</code> is the top-left corner. Returns a new image.
	 * 
	 * @param x The leftmost coordinate of the rectangle to extract
	 * @param y The topmost coordinate of the rectangle to extract
	 * @param w The width of the rectangle to extract
	 * @param h The height of the rectangle to extract
	 * @return A new image representing the selected region
	 */
	public abstract I extractROI(int x, int y, int w, int h);

	/**
	 * Extract a rectangular region of interest of the given width and height.
	 * Coordinate <code>(0,0)</code> is the top-left corner. Returns a new image.
	 * 
	 * @param r the rectangle 
	 * @return A new image representing the selected region
	 */
	public I extractROI(Rectangle r) {
		return extractROI((int)r.x, (int)r.y, (int)r.width, (int)r.height);
	}
	
	/**
	 * Fill this image with the given colour. Should overwrite all other
	 * data stored in this image. Side-affects this image.
	 * 
	 * @param colour the colour to fill the image with
	 * @return A reference to this image.
	 */
	public abstract I fill(Q colour);

	/**
	 * Get a rectangle representing the image, with the top-left
	 * at 0,0 and the bottom-right at width,height
	 * @return the bounding rectangle of the image
	 */
	public Rectangle getBounds(){
		return new Rectangle(0,0,this.getWidth(),this.getHeight());
	}

	/**
	 * Get the image width in pixels. This is syntactic 
	 * sugar for {@link #getWidth()};
	 * 
	 * @return The image width in pixels.
	 */
	public int getCols() {
		return getWidth();
	}

	/**
	 *	Get bounding box of non-zero-valued pixels around the outside of
	 *	the image. Used by {@link #trim()}.
	 * 
	 *  @return A  rectangle of the boundaries of the non-zero-valued image
	 */
	public abstract Rectangle getContentArea();
	
	/**
	 * Get the given field of this image. Used for deinterlacing video, this
	 * should return a new image containing the deinterlaced image. The returned 
	 * image will be half the height of this image.
	 * 
	 * @param f The {@link Field} to extract from this image
	 * @return An image containing only the odd or even fields.
	 */
	public abstract I getField(Field f);
	
	/**
	 * Get the given field of this image, maintaining the image's aspect ratio
	 * by doubling the fields. Used for deinterlacing video, this should return
	 * a new image containing the deinterlaced image. The returned image should
	 * be the same size as this image.
	 * 
	 * @param f The {@link Field} to extract from this image
	 * @return An image containing the odd or even fields doubled.
	 */
	public abstract I getFieldCopy(Field f);

	/**
	 * Get the given field of this image, maintaining the image's aspect
	 * ratio by interpolating between the fields. Used for deinterlacing
	 * video, this should return a new image containing the detinterlaced image.
	 * The returned image should be the same size as this image.
	 * 
	 * @param f The {@link Field} to extract from this image.
	 * @return An image containing the odd or even fields with interpolated
	 * 		rows between.
	 */
	public abstract I getFieldInterpolate(Field f);
	
	/**
	 * Returns the image height in pixels.
	 * 
	 * @return The image height in pixels.
	 */
	public abstract int getHeight();
	
	/**
	 * Get the value of the pixel at coordinate <code>(x, y)</code>.
	 * 
	 * @param x The x-coordinate to get
	 * @param y The y-coordinate to get
	 * 
	 * @return The pixel value at (x, y)
	 */
	public abstract Q getPixel(int x, int y);
	
	/**
	 *	Returns a pixel comparator that is able to compare equality of pixels
	 *	in the given image type.
	 * 
	 *	@return A {@link Comparator} that compares pixels.
	 */
	public abstract Comparator<? super Q> getPixelComparator() ;
	
	/**
	 * Get the value of a sub-pixel using linear-interpolation.
	 * 
	 * @param x The x-coordinate to get
	 * @param y The y-coordinate to get
	 * @return The value of the interpolated point at <code>(x,y)</code>
	 */
	public abstract Q getPixelInterp(double x, double y);
	
	/**
	 * Get the value of a sub-pixel using linear-interpolation. Also specify the colour of the background (for interpolation at the edge)
	 * 
	 * @param x The x-coordinate to get.
	 * @param y The y-coordinate to get.
	 * @param backgroundColour The colour of the background pixel.
	 * @return The value of the interpolated point at <code>(x,y)</code>
	 */
	public abstract Q getPixelInterp(double x, double y, Q backgroundColour);
	
	/**
	 * 	Returns the pixels in this image as a vector (an array of the pixel
	 * 	type).
	 *  
	 *  @param f The array into which to place the data
	 *  @return The pixels in the image as a vector (a reference to the given array).
	 */
	public Q[] getPixelVector( Q[] f )
	{
		for( int y = 0; y < getHeight(); y++ )
			for( int x = 0; x < getWidth(); x++ )
				f[x+y*getWidth()] = getPixel(x,y);
		
		return f;
	}
	
	/**
	 * Get the height of this image. This is a syntactic sugar method for
	 * {@link #getHeight()}.
	 * 
	 * @return The image height in pixels.
	 */
	public int getRows() {
		return getHeight();
	}
	
	/**
	 * Get the width (number of columns) in this image.
	 *  
	 * @return the image width
	 */
	public abstract int getWidth();

	/**
	 * Copy the internal state from another image of the same type.
	 * 
	 * @param im The source image to make a copy of.
	 * @return A reference to this image.
	 */
	public abstract I internalAssign(I im);
	
	/**
	 * Copy pixels from given ARGB buffer image into this image. Side-affects this
	 * image.
	 * 
	 * @param pixelData buffer of ARGB packed integer pixels 
	 * @param width the width of the buffer
	 * @param height the height of the buffer  
	 * 
	 * @return A reference to this image.
	 */
	public abstract I internalAssign(int [] pixelData, int width, int height);
	
	/**
	 * Invert the image pixels by finding the maximum value and subtracting
	 * each pixel value from that maximum. 
	 * 
	 * @return A reference to this image.
	 */
	public abstract I inverse();
	
	/**
	 * Find the maximum pixel value.
	 * 
	 * @return The maximum pixel value
	 */
	public abstract Q max();
	
	/**
	 * Find the minimum pixel value.
	 * 
	 * @return The minimum pixel value
	 */
	public abstract Q min();
	
	/**
	 * Multiply the pixel values in this image with the corresponding pixel
	 * values in the given image. This method returns a new image.
	 * 
	 * @param im The image to multiply with this one
	 * @return A new image containing the result.
	 */
	public I multiply(Image<?,?> im) {
		I newImage = this.clone();
		newImage.multiplyInline(im);
		return newImage;
	}
	
	/**
	 * Multiply each pixel of this by the given scalar and return new image.
	 * 
	 * @param num The scalar which to multiply the image by 
	 * @return A new image containing the result
	 */
	public I multiply(Q num) {
		I newImage = this.clone();
		newImage.multiplyInline(num);
		return newImage;
	}
	
	/**
	 * Multiply each pixel in this image by the corresponding pixel in
	 * the given image. This method side-affects this image.
	 * 
	 * @param im The image to multiply with this image.
	 * @return A reference to this image.
	 */
	public abstract I multiplyInline(Image<?,?> im);
	
	/**
	 * Multiply each pixel of this by the given scalar. This method side-affects
	 * this image.
	 * 
	 * @param num The scalar to multiply this image by.
	 * @return A reference to this image.
	 */
	public abstract I multiplyInline(Q num);
	
	/**
	 * Create a new instance of this image subclass with given dimensions.
	 * @param width The image width
	 * @param height The image height
	 * 
	 * @return A new instance of an image of type <code>I</code>
	 */
	public abstract I newInstance(int width, int height);
	
	/**
	 * Normalise all pixel values to fall within the range 0.0 - 1.0. This should
	 * be scaled by both the maximum and minimum values. This method side-affects
	 * this image.
	 * 
	 * @return A reference to this image.
	 */
	public abstract I normalise();
	
	/**
	 * Adds padding as in {@link FImage#padding}. The padding colour is the colour of the closest border pixel.
	 * @param paddingWidth padding in the x direction
	 * @param paddingHeight padding in the y direction
	 * @return padded image
	 */
	public I padding(int paddingWidth, int paddingHeight) {
		return this.padding(paddingWidth, paddingHeight,null);
	}

	/**
	 * Adds this many pixels to both sides of the image such that the new image width = padding + width + padding
	 * with the original image in the middle
	 * @param paddingWidth left and right padding width
	 * @param paddingHeight top and bottom padding width
	 * @param paddingColour colour of padding, if null the closes border pixel is used
	 * @return padded image
	 */
	public I padding(int paddingWidth, int paddingHeight, Q paddingColour) {
		I out = this.newInstance(paddingWidth + this.getWidth() + paddingWidth, paddingHeight + this.getHeight() + paddingHeight);
		I clone = this.clone();
		out.createRenderer().drawImage(clone, paddingWidth, paddingHeight);
		int rightLimit = paddingWidth+this.getWidth();
		int bottomLimit = paddingHeight+this.getHeight();
		// Fill the padding with a colour if it isn't null
		if(paddingColour != null)
			for(int y = 0;y<out.getHeight();y++){
				for(int x = 0;x<out.getWidth();x++){
					if(x>=paddingWidth&&x<rightLimit&&y>=paddingHeight&&y<bottomLimit) continue;
					out.setPixel(x, y, paddingColour);
				}
			}
		else
			for(int y = 0;y<out.getHeight();y++){
				for(int x = 0;x<out.getWidth();x++){
					if(x>=paddingWidth&&x<rightLimit&&y>=paddingHeight&&y<bottomLimit) continue;
					if(x < paddingWidth && y < paddingHeight) 
						out.setPixel(x, y, this.getPixel(0, 0)); // Top Left
					else if(x < paddingWidth && y >= bottomLimit) 
						out.setPixel(x, y, this.getPixel(0, this.getHeight()-1)); // Bottom Left
					else if(x >= rightLimit && y < paddingHeight) 
						out.setPixel(x, y, this.getPixel(this.getWidth()-1, 0)); // Top Right
					else if(x >= rightLimit && y >= bottomLimit) 
						out.setPixel(x, y, this.getPixel(this.getWidth()-1, this.getHeight()-1)); // Bottom Right
					else{
						if(x < paddingWidth) out.setPixel(x, y, this.getPixel(0, y-paddingHeight)); // Left
						else if(x >= rightLimit) out.setPixel(x, y, this.getPixel(this.getWidth()-1, y-paddingHeight)); // Right
						else if(y < paddingHeight) out.setPixel(x, y, this.getPixel(x-paddingWidth, 0)); // Top
						else if(y >= bottomLimit) out.setPixel(x, y, this.getPixel(x-paddingWidth, this.getHeight()-1)); // Bottom
					}
				}
			}
			
		return out;
	}
	
	/**
	 * Process this image with the given {@link GridProcessor} and return new 
	 * image containing the result.
	 * 
	 * @param p {@link GridProcessor} to apply to this image.
	 * @return A new image containing the result.
	 */
	public I process(GridProcessor<Q,I> p) {
		int height = p.getVericalGridElements();
		int width = p.getHorizontalGridElements();
		I newImage = this.newInstance( width, height );
		newImage.zero();
		
		int gridWidth = getWidth()/width;
		int gridHeight = getHeight()/height;
		for( int y = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				newImage.setPixel( x, y, p.processGridElement( this.extractROI( gridWidth*x, gridHeight*y, gridWidth, gridHeight ) ) );
		
		return newImage;
	}

	/**
	 * Process this image with an {@link ImageProcessor} and return new image
	 * containing the result.
	 * 
	 * @param p The {@link ImageProcessor} to apply to this image.
	 * @return A new image containing the result.
	 */
	public I process(ImageProcessor<I> p)  {
		I newImage = this.clone();
		newImage.processInline(p);
		return newImage;
	}
	
	/**
	 * Process this image with an {@link ImageProcessor} and an optional set
	 * of extra images, and return new image containing the result.
	 * 
	 * @param p The {@link ImageProcessor} to apply.
	 * @param images Extra images that are passed to the processor.
	 * @return A new image containing the result.
	 */
	public I process(ImageProcessor<I> p, Image<?,?>... images) {
		I newImage = this.clone();
		newImage.processInline(p, images);
		return newImage;
	}
	
	/**
	 * Process this image with the given {@link KernelProcessor} and 
	 * return new image containing the result.
	 * 
	 * @param p The {@link KernelProcessor} to apply.
	 * @return A new image containing the result.
	 */
	public I process(KernelProcessor<Q,I> p) {
		return process(p, false);
	}

	/**
	 * Process this image with the given {@link KernelProcessor} and 
	 * return new image containing the result.
	 * 
	 * @param p The {@link KernelProcessor} to apply.
	 * @param pad Should the image be zero padded so the 
	 * 	kernel reaches the edges of the output
	 * @return A new image containing the result.
	 */ 
	public I process(KernelProcessor<Q,I> p, boolean pad) {
		I newImage = this.clone();
		newImage.zero();
		
		int kh = p.getKernelHeight();
		int kw = p.getKernelWidth();
		
		int hh = p.getKernelHeight() / 2;
		int hw = p.getKernelWidth() / 2;
		
		I tmp = newInstance( kw, kh );
		
		if (!pad) {
			for( int y = hh; y < getHeight() - (kh - hh); y++ ) {
				for( int x = hw; x < getWidth() - (kw - hw); x++ ) {
					newImage.setPixel(x, y, p.processKernel( this.extractROI( x - hw, y - hh, tmp ) ));
				}
			}
		} else {
			for( int y = 0; y < getHeight(); y++ ) {
				for( int x = 0; x < getWidth(); x++ ) {
					newImage.setPixel(x, y, p.processKernel( this.extractROI( x - hw, y - hh, tmp ) ));
				}
			}
		}
		
		return newImage;
	}
	
	/**
	 * 	Process this image with the given {@link PixelProcessor} and return
	 * 	a new image containing the result.
	 * 
	 *  @param p The {@link PixelProcessor} to apply.
	 *  @return A new image containing the result.
	 */
	public I process(PixelProcessor<Q> p) {
		I newImage = this.clone();
		newImage.processInline(p);
		return newImage;
	}
	
	/**
	 * 	Process this image with the given {@link PixelProcessor} with an optional
	 * 	set of images and return a new image containing the result.
	 * 
	 *  @param p The {@link PixelProcessor} to apply.
	 *  @param images Extra set of images to pass to the processor.
	 *  @return A new image containing the result.
	 */
	public I process(PixelProcessor<Q> p, Image<?,?>... images) {
		I newImage = this.clone();
		newImage.processInline(p, images);
		return newImage;
	}
	
	/**
	 * 	Process this image with the given {@link ImageProcessor} side-affecting
	 * 	this image.
	 * 
	 *  @param p The {@link ImageProcessor} to apply.
	 *  @return A reference to this image containing the result.
	 */
	@SuppressWarnings("unchecked")
	public I processInline(ImageProcessor<I> p) {
		p.processImage((I)this);
		return (I)this;
	}
	
	/**
	 *	Process this image with the given {@link ImageProcessor} and an optional
	 *	set of images, side-affecting this image.
	 * 
	 *  @param p The {@link ImageProcessor} to apply.
	 *  @param images Extra set of image to pass to the processor.
	 *  @return A reference to this image containing the result.
	 */
	@SuppressWarnings("unchecked")
	public I processInline(ImageProcessor<I> p, Image<?,?>... images) {
		p.processImage((I)this, images);
		return (I)this;
	}
	
	/**
	 * 	Process this image with the given {@link KernelProcessor} side-affecting
	 * 	this image.
	 * 
	 *  @param p The {@link KernelProcessor} to apply.
	 *  @return A reference to this image containing the result.
	 */
	public I processInline(KernelProcessor<Q,I> p) {
		return processInline(p, false);
	}
	
	/**
	 * 	Process this image with the given {@link KernelProcessor} side-affecting
	 * 	this image.
	 * 
	 *  @param p The {@link KernelProcessor} to apply.
	 *  @param pad Should the image be zero padded so the 
	 * 	  kernel reaches the edges of the output
	 *  @return A reference to this image containing the result.
	 */
	@SuppressWarnings("unchecked")
	public I processInline(KernelProcessor<Q,I> p, boolean pad) {
		I newImage = process(p, pad);
		this.internalAssign(newImage);
		return (I)this;
	}
	
	/**
	 * 	Process this image with the given {@link PixelProcessor} side-affecting
	 * 	this image.
	 * 
	 *  @param p The {@link PixelProcessor} to apply.
	 *  @return A reference to this image containing the result.
	 */
	@SuppressWarnings("unchecked")
	public I processInline(PixelProcessor<Q> p) {
		for (int y=0; y<getHeight(); y++) {
			for (int x=0; x<getWidth(); x++) {
				setPixel(x, y, p.processPixel(getPixel(x,y)));
			}
		}
		
		return (I)this;
	}
	
	
	/**
	 * 	Process this image with the given {@link PixelProcessor} and an optional
	 * 	set of images, side-affecting this image.
	 * 
	 *  @param p The {@link PixelProcessor} to apply.
	 *  @param images Extra set of images that are passed to the processor.
	 *  @return A reference to this image containing the result.
	 */
	@SuppressWarnings("unchecked")
	public I processInline(PixelProcessor<Q>p, Image<?,?>... images) {
		Number[] otherpixels = new Number[images.length];
		for (int y=0; y<getHeight(); y++) {
			for (int x=0; x<getWidth(); x++) {
				for (int i=0; i<images.length; i++) otherpixels[i] = (Number)images[i].getPixel(x, y);
				setPixel(x, y, p.processPixel(getPixel(x,y), otherpixels));
			}
		}
		return (I)this;
	}
	
	/**
	 * 	Process this image with the given {@link PixelProcessor} only affecting
	 * 	those pixels where the mask is non-zero. Returns a new image.
	 * 
	 *  @param mask The mask to apply to the processing.
	 *  @param p The {@link PixelProcessor} to apply.
	 *  @return A new image containing the result.
	 */
	public I processMasked(FImage mask, PixelProcessor<Q> p) {
		I newImage = this.clone();
		newImage.processMaskedInline(mask, p);
		return newImage;
	}
	
	/**
	 * 	Process this image with the given {@link PixelProcessor} and an extra
	 * 	set of optional images, only affecting those pixels where the mask is 
	 * 	non-zero. Returns a new image.
	 * 
	 *  @param mask The mask to apply to the processor.
	 *  @param p The {@link PixelProcessor} to apply.
	 *  @param images An optional set of extra images passed to the processor.
	 *  @return A new image containing the result.
	 */
	public I processMasked(FImage mask, PixelProcessor<Q> p, Image<?,?>... images) {
		I newImage = this.clone();
		newImage.processMaskedInline(mask, p, images);
		return newImage;
	}
	
	/**
	 * 	Process this image with the given {@link PixelProcessor} and an extra
	 * 	set of optional images, only affecting those pixels where the mask is 
	 * 	non-zero. Side-affects this image.
	 * 
	 *  @param mask The mask to apply to the processor.
	 *  @param p The {@link PixelProcessor} to apply.
	 *  @param images An optional set of extra images passed to the processor.
	 *  @return A reference to this image containing the result.
	 */
	@SuppressWarnings("unchecked")
	public I processMaskedInline(FImage mask, PixelProcessor<Q>p, Image<?,?>... images) {
		Number[] otherpixels = new Number[images.length];
		for (int y=0; y<getHeight(); y++) {
			for (int x=0; x<getWidth(); x++) {
				if (mask.pixels[y][x] == 0) continue;
				for (int i=0; i<images.length; i++) otherpixels[i] = (Number)images[i].getPixel(x, y);
				setPixel(x, y, p.processPixel(getPixel(x,y), otherpixels));
			}
		}
		return (I)this;
	}
	
	/**
	 * Sets the pixel at <code>(x,y)</code> to the given value. Side-affects
	 * this image.
	 * 
	 * @param x The x-coordinate of the pixel to set
	 * @param y The y-coordinate of the pixel to set
	 * @param val The value to set the pixel to.
	 */
	public abstract void setPixel(int x, int y, Q val);
	
	/**
	 * Subtract the corresponding pixel value from the given image from the
	 * pixel values in this image. Returns a new image.
	 * 
	 * @param im The image to subtract from this image.
	 * @return A new image containing the result.
	 */
	public I subtract(Image<?,?> im) {
		I newImage = this.clone();
		newImage.subtractInline(im);
		return newImage;
	}

	/**
	 * Subtract a scalar from every pixel value in this image and return 
	 * new image.
	 * 
	 * @param num A value to subtract from each pixel. 
	 * @return A new image containing the result.
	 */
	public I subtract(Q num) {
		I newImage = this.clone();
		newImage.subtractInline(num);
		return newImage;
	}
	
	/**
	 * Subtract the corresponding pixel value from the given image from the
	 * pixel values in this image. Side-affects this image.
	 * 
	 * @param im The image to subtract from this image.
	 * @return A reference to this containing the result.
	 */
	public abstract I subtractInline(Image<?,?> im);
	
	/**
	 * Subtract a scalar from every pixel value in this image. Side-affects
	 * this image.
	 * 
	 * @param num A value to subtract from each pixel. 
	 * @return A reference to this image containing the result.
	 */
	public abstract I subtractInline(Q num);
	
	/**
	 * Set all values less than the given threshold to 0 and all others to 1.
	 * Side-affects this image.
	 * 
	 * @param thresh The threshold value 
	 * @return A reference to this image containing the result.
	 */
	public abstract I threshold(Q thresh);
	
	/**
	 * Convert the image to a byte representation suitable for writing to a pnm
	 * type format. Each byte should represent a single pixel. Multiband images 
	 * should interleave the data; e.g. [R1,G1,B1,R2,G2,B2...etc.]
	 * 
	 * @return This image as a byte array
	 */
	public abstract byte[] toByteImage();
	
	/**
	 * 	Returns a 1D array representation of this image with each pixel
	 *  represented as a packed ARGB integer.
	 * 
	 *  @return An array of ARGB pixels.
	 */
	public abstract int [] toPackedARGBPixels();
	
	/**
	 * Apply a transform matrix to the image and returns the
	 * result as a new image.
	 * 
	 * @param transform The transform matrix to apply.
	 * @return A new image containing the result.
	 */
	public I transform(Matrix transform) {
		boolean unset = true;
		double minX=0,minY=0,maxX=0,maxY=0;
		double[][][] extrema = new double[][][]{
				{{0},{0},{1}},
				{{0},{this.getHeight()},{1}},
				{{this.getWidth()},{0},{1}},
				{{this.getWidth()},{this.getHeight()},{1}},
		};
		for(double[][] ext : extrema){
			Matrix tmp = transform.times(Matrix.constructWithCopy(ext));
			if(unset)
			{
				minX = maxX = tmp.get(0, 0);
				maxY = minY = tmp.get(1, 0);
				unset = false;
			}
			else{
				if(tmp.get(0, 0) > maxX) maxX = tmp.get(0, 0);
				if(tmp.get(1, 0) > maxY) maxY = tmp.get(1, 0);
				if(tmp.get(0, 0) < minX) minX = tmp.get(0, 0);
				if(tmp.get(1, 0) < minY) minY = tmp.get(1, 0);
			}
		}
		I output = this.newInstance((int)(Math.abs(maxX - minX)),(int)(Math.abs(maxY-minY)));
		Matrix invTrans = transform.inverse();
		for(int x = 0; x < output.getWidth(); x++){
			for(int y = 0; y < output.getHeight(); y++){
				
				Matrix pre = Matrix.constructWithCopy(
						new double[][]{{x},{y},{1.0}}
				);
				Matrix post = invTrans.times(pre);
				double oldx = post.get(0, 0);
				double oldy = post.get(1, 0);
				if(oldx < 0 || oldx >= this.getWidth() || oldy < 0 || oldy >= this.getHeight()) continue;
				output.setPixel(x, y, this.getPixelInterp(oldx, oldy));
			}
		}
		return output;
	}
	
	/**
	 *	Removes zero-valued pixels from around the outside of
	 *	the image. Analagous to {@link String#trim()}.
	 * 
	 *  @return A new image containing the trimmed image.
	 */
	public I trim() {
		Rectangle rect = this.getContentArea();
		return this.extractROI((int)rect.minX(), (int)rect.minY(), (int)(rect.getWidth()), (int)(rect.getHeight()));
	}
	
	/**
	 * Set all pixels in the image to zero. Side-affects this image.
	 * 
	 * @return A reference to this image containing the result.
	 */
	public abstract I zero();
}

