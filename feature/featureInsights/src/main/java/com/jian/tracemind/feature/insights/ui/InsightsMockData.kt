package com.jian.tracemind.feature.insights.ui

object InsightsMockData {
    // 18 weeks × 7 days heatmap (0–4 intensity)
    val heatmapData: List<List<Int>> = listOf(
        listOf(0,2,1,3,0,4,2), listOf(1,0,3,2,4,1,0), listOf(2,1,0,3,2,1,4), listOf(0,4,2,1,3,0,2),
        listOf(3,1,2,0,4,2,1), listOf(0,0,3,2,1,4,0), listOf(2,3,1,0,2,3,1), listOf(4,2,0,3,1,2,0),
        listOf(1,0,4,2,3,1,2), listOf(0,3,2,4,1,0,3), listOf(2,1,3,0,2,4,1), listOf(0,2,1,3,4,2,0),
        listOf(3,4,0,2,1,3,2), listOf(1,2,4,0,3,1,2), listOf(0,1,2,3,0,4,2), listOf(3,2,1,0,4,2,1),
        listOf(2,0,3,4,1,2,0), listOf(1,3,2,0,4,1,3)
    )
    
    val heatmapAlpha = listOf(0.06f, 0.22f, 0.42f, 0.65f, 1.0f)
    val monthAtWeek = mapOf(0 to "1月", 3 to "2月", 6 to "3月", 9 to "4月", 12 to "5月", 15 to "6月")
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    val moodDataList = listOf(
        MoodData("😌", "平静", 14, 0xFFA8D5BA),
        MoodData("🤩", "兴奋", 8, 0xFFFFD580),
        MoodData("😊", "开心", 11, 0xFFB5C4F5),
        MoodData("😔", "低落", 4, 0xFFF2B8A0),
        MoodData("🌙", "沉思", 9, 0xFFC4B5F5)
    )

    val tagCloudData = listOf(
        TagData("#感恩", 45),
        TagData("#旅行", 34),
        TagData("#学习", 27),
        TagData("#日本", 22),
        TagData("#阅读", 19),
        TagData("#工作", 18),
        TagData("#清晨", 15),
        TagData("#自然", 12),
        TagData("#朋友", 11),
        TagData("#美食", 9),
        TagData("#梦境", 9),
        TagData("#音乐", 7)
    )
}
