//
//  RollingStockImageView.swift
//  NextTrainWidgetExtension
//
//  Created by Amanshu Raikwar on 28/10/22.
//

import SwiftUI

// scales, crops and draws the rolling stock images of a train
// TRY NOT TO TOUCH, logic is tricky AF -_-

struct RollingStockImageView: View {
    let rollingStockImages: [UIImage]
    let maximumViewportWidth: CGFloat
    
    var body: some View {
        HStack(
            spacing: 0
        ) {
            ForEach(rollingStockImages, id: \.self) { rollingStockImage in
                // we need to resize and crop the image
                // because ios wouldn't let us display a full sized image
                // in a widget
                let resizedImage = cropImage(
                    imageToCrop: rollingStockImage.imageScaledToSize(
                        toHeight: 40
                    )!,
                    width: maximumViewportWidth
                )
                
                // for some reason, the width of the resized image is
                // not the same as geometry width, even if we cropped it
                // to geometry width
                // so we need to manually check and force the width
                let width = maximumViewportWidth > resizedImage.size.width
                                ? resizedImage.size.width : maximumViewportWidth

                Image(
                    uiImage: resizedImage
                )
                .resizable()
                .frame(width: width, height: 40)
            }
        }
    }
    
    func cropImage(
        imageToCrop: UIImage,
        width: CGFloat
    ) -> UIImage {
        // if the current image's width is alr smaller than the proposed cropped width
        // just use the current image without cropping
        if (CGFloat(imageToCrop.cgImage!.width) < width) {
            return imageToCrop
        }
        
        let scaleX = imageToCrop.size.width / CGFloat(imageToCrop.cgImage!.width)
        
        let rect = CGRect(
            x: 0,
            y: 0,
            width: Int(width / scaleX),
            height: imageToCrop.cgImage!.height
        )
        let imageRef = imageToCrop.cgImage!.cropping(to: rect)!
        let cropped = UIImage(cgImage: imageRef)
        return cropped
    }
}

extension UIImage {
        // returns a scaled version of the image
        func imageScaledToSize(
            toHeight height: CGFloat,
            isOpaque: Bool = false
        ) -> UIImage? {
            let size = CGSize(
                width: CGFloat(ceil(height * size.width / size.height)),
                height: height
            )
            
            // begin a context of the desired size
            UIGraphicsBeginImageContextWithOptions(size, isOpaque, 0.0)

            // draw image in the rect with zero origin and size of the context
            let imageRect = CGRect(origin: CGPointZero, size: size)
            self.draw(in: imageRect)

            // get the scaled image, close the context and return the image
            let scaledImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()

            return scaledImage
       }
}
