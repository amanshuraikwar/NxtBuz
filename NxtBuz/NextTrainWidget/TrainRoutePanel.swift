import SwiftUI

/// Each time column describes one journey between the two station rows.
struct TrainRoutePanel: View {
    struct Times {
        let departure: String
        let arrival: String
        let platform: String?
        var detail: String? = nil
    }

    let from: String
    let to: String
    let times: [Times]
    let accent: Color
    let primary: Color
    let secondary: Color
    let isDark: Bool
    var splitFlapDepartures = false

    private var columnWidth: CGFloat? { times.count > 1 ? (splitFlapDepartures ? 83 : 65) : nil }

    var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 4) {
                Text(from).font(NxtBuzFonts.body).fontWeight(.medium).lineLimit(1).minimumScaleFactor(0.65)
                if times.count == 1, let platform = times.first?.platform { badge(platform) }
                Spacer(minLength: 2)
                ForEach(times.indices, id: \.self) { i in
                    HStack(spacing: 2) {
                        if times.count > 1, let platform = times[i].platform { badge(platform) }
                        if splitFlapDepartures {
                            SplitFlapTime(time: times[i].departure)
                        } else {
                            Text(times[i].departure).font(NxtBuzFonts.bodyMonospaced).bold()
                        }
                    }
                    .frame(minWidth: columnWidth, alignment: .trailing)
                }
            }
            .padding(.horizontal, 8).padding(.vertical, 6)
            .foregroundStyle(isDark ? Color(.systemGray6) : .white)
            .background(accent, in: RoundedRectangle(cornerRadius: 14))
            .padding(.horizontal, 8).padding(.top, 8)
            HStack(spacing: 4) {
                Text(to).font(NxtBuzFonts.body).lineLimit(1).minimumScaleFactor(0.65)
                Spacer(minLength: 2)
                ForEach(times.indices, id: \.self) { i in
                    VStack(alignment: .trailing, spacing: 0) {
                        Text(times[i].arrival).font(NxtBuzFonts.bodyMonospaced).bold()
                        if let detail = times[i].detail { Text(detail).font(NxtBuzFonts.captionSmall) }
                    }
                    .frame(minWidth: columnWidth, alignment: .trailing)
                }
            }
            .foregroundStyle(isDark ? secondary : Color(.systemGray5))
            .padding(.horizontal, 16).padding(.vertical, 8)
        }
        .background(LinearGradient(colors: isDark ? [Color(.systemGray5), Color(.systemGray6)] : [primary, secondary],
                                   startPoint: .top, endPoint: .bottom), in: RoundedRectangle(cornerRadius: 14))
    }

    private func badge(_ platform: String) -> some View {
        Text(platform.uppercased()).font(NxtBuzFonts.captionSmall).bold()
            .padding(.horizontal, 2).foregroundStyle(accent)
            .background(isDark ? Color(.systemGray6) : .white, in: RoundedRectangle(cornerRadius: 2))
            .accessibilityLabel("Platform \(platform)")
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
        .padding(2)
        .background(Color(white: 0.055), in: RoundedRectangle(cornerRadius: 3))
        .overlay {
            RoundedRectangle(cornerRadius: 3).strokeBorder(.white.opacity(0.19), lineWidth: 0.5)
        }
        .shadow(color: .black.opacity(0.28), radius: 0.6, y: 0.6)
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
