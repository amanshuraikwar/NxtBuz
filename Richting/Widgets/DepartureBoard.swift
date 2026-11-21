import SwiftUI

struct DepartureBoard: View {
    let station: String
    let journeys: [Journey]

    var body: some View {
        HStack(alignment: .top, spacing: 8) {
            Text(station)
                .font(NxtBuzFonts.body.weight(.medium))
                .lineLimit(2).minimumScaleFactor(0.75)
            Spacer(minLength: 0)
            ForEach(journeys) { journey in
                VStack(alignment: .trailing, spacing: 4) {
                    HStack(spacing: 4) {
                        if let platform = journey.firstLeg.platform {
                            Text(platform.uppercased())
                                .font(NxtBuzFonts.captionSmall.bold())
                                .padding(.horizontal, 3)
                                .foregroundStyle(.black)
                                .background(.white, in: RoundedRectangle(cornerRadius: 2))
                                .accessibilityLabel("Platform \(platform)")
                        }
                        SplitFlapTime(time: journey.departure)
                    }
                    Text(journey.transferSummary)
                        .font(NxtBuzFonts.captionSmall)
                        .foregroundStyle(Color(white: 0.75))
                }
                .fixedSize()
            }
        }
    }
}

private struct SplitFlapTime: View {
    let time: String

    var body: some View {
        HStack(spacing: 1) {
            ForEach(Array(time.enumerated()), id: \.offset) { _, character in
                if character == ":" {
                    VStack(spacing: 4) {
                        Circle().frame(width: 2, height: 2)
                        Circle().frame(width: 2, height: 2)
                    }
                    .foregroundStyle(Color(white: 0.85))
                    .frame(width: 3)
                } else {
                    FlapDigit(character: character)
                }
            }
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("Departs at \(time)")
    }
}

private struct FlapDigit: View {
    let character: Character
    private let height: CGFloat = 24

    var body: some View {
        // Crop the same printed numeral onto both leaves so it registers across the hinge.
        VStack(spacing: 0.75) {
            leaf(upper: true)
            leaf(upper: false)
        }
        .frame(width: 13, height: height)
        .clipShape(RoundedRectangle(cornerRadius: 1.25))
    }

    private func leaf(upper: Bool) -> some View {
        let ink = Color(white: upper ? 0.98 : 0.89)
        let surface = upper ? [Color(white: 0.26), Color(white: 0.14)]
                            : [Color(white: 0.19), Color(white: 0.10)]
        return Text(String(character))
            .font(.custom("HelveticaNeue-CondensedBold", fixedSize: 23))
            .foregroundStyle(ink)
            .frame(width: 13, height: height)
            .frame(height: (height - 0.75) / 2, alignment: upper ? .top : .bottom)
            .clipped()
            .background(LinearGradient(colors: surface, startPoint: .top, endPoint: .bottom))
            .overlay(alignment: .top) {
                Rectangle().fill(.white.opacity(upper ? 0.13 : 0.20)).frame(height: 0.4)
            }
    }
}
