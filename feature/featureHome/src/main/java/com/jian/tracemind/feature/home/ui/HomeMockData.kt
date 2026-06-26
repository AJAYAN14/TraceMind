package com.jian.tracemind.feature.home.ui

data class Memory(
    val id: Int,
    val title: String,
    val preview: String,
    val date: String,
    val mood: String,
    val location: String,
    val imgUrl: String
)

data class Folder(
    val id: Int,
    val title: String,
    val icon: String,
    val count: Int
)

object HomeMockData {
    val memories = listOf(
        Memory(
            id = 1,
            title = "京都的黄金时刻",
            preview = "光线恰到好处地穿透竹林。我就那样静静坐了一个小时，只是呼吸，感受这一切。",
            date = "2026年6月22日",
            mood = "😌",
            location = "日本·京都",
            imgUrl = "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=200&h=200&fit=crop&auto=format"
        ),
        Memory(
            id = 2,
            title = "工作室的第一天",
            preview = "空气里弥漫着新鲜油漆的气息。我的桌子朝着一扇俯瞰运河的窗户。",
            date = "2026年6月20日",
            mood = "🤩",
            location = "荷兰·阿姆斯特丹",
            imgUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?w=200&h=200&fit=crop&auto=format"
        ),
        Memory(
            id = 3,
            title = "深夜洋甘菊茶",
            preview = "睡不着，泡了杯茶，然后一口气写了三个小时。有些夜晚就是这样。",
            date = "2026年6月18日",
            mood = "🌙",
            location = "家里",
            imgUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=200&h=200&fit=crop&auto=format"
        ),
        Memory(
            id = 4,
            title = "集市清晨",
            preview = "发现了最漂亮的传家宝番茄，和农夫聊了整整三十分钟，收获颇丰。",
            date = "2026年6月15日",
            mood = "😊",
            location = "英国·伦敦",
            imgUrl = "https://images.unsplash.com/photo-1488459716781-31db52582fe9?w=200&h=200&fit=crop&auto=format"
        )
    )

    val foldersData = listOf(
        Folder(id = 1, title = "旅行", icon = "✈️", count = 34),
        Folder(id = 2, title = "工作", icon = "💼", count = 18),
        Folder(id = 3, title = "学习", icon = "📚", count = 27),
        Folder(id = 4, title = "梦境", icon = "🌙", count = 9),
        Folder(id = 5, title = "感恩", icon = "🌱", count = 45)
    )
}
