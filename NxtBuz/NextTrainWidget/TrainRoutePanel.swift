import SwiftUI

/// Each time column describes one direct service between the two station rows.
struct TrainRoutePanel: View {
    struct Times {
        let departure: String
        let arrival: String
        let platform: String?
    }

    let from: String
    let to: String
    let times: [Times]
    let accent: Color
    let primary: Color
    let secondary: Color
    let isDark: Bool

    var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 4) {
                Text(from).font(NxtBuzFonts.body).fontWeight(.medium).lineLimit(1).minimumScaleFactor(0.65)
                if times.count == 1, let platform = times.first?.platform { badge(platform) }
                Spacer(minLength: 2)
                ForEach(times.indices, id: \.self) { i in
                    HStack(spacing: 2) {
                        if times.count > 1, let platform = times[i].platform { badge(platform) }
                        Text(times[i].departure).font(NxtBuzFonts.bodyMonospaced).bold()
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
                    Text(times[i].arrival).font(NxtBuzFonts.bodyMonospaced).bold()
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
