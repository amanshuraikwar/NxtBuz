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
                    .frame(minWidth: times.count > 1 ? 65 : nil, alignment: .trailing)
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
                    .frame(minWidth: times.count > 1 ? 65 : nil, alignment: .trailing)
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
                    Text(":").frame(width: 4)
                } else {
                    Text(String(character))
                        .frame(width: 11, height: 21)
                        .background(
                            LinearGradient(colors: [Color(white: 0.23), Color(white: 0.08)],
                                           startPoint: .top, endPoint: .bottom),
                            in: RoundedRectangle(cornerRadius: 2)
                        )
                        .overlay { Rectangle().fill(.black.opacity(0.85)).frame(height: 1) }
                }
            }
        }
        .font(.system(size: 16, weight: .semibold, design: .monospaced))
        .foregroundStyle(Color(red: 0.99, green: 0.96, blue: 0.86))
        .padding(2)
        .background(Color(white: 0.07), in: RoundedRectangle(cornerRadius: 3))
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("Departs at \(time)")
    }
}
