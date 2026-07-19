# TraceMind Editor 深色模式设计规范

本规范结合了 **`apple-design`**（注重物理动效、材质与深度）和 **`ui-ux-pro-max`**（注重极致对比度、可用性与专业 UI）两大核心思想，专为 Android (Jetpack Compose) 打造。

## 1. 颜色与表面层级 (Surfaces & Elevation)

在深色模式下，**不要使用纯黑 (`#000000`)** 作为背景，这会导致 OLED 屏幕拖影，并且与明亮文字对比度过高导致视觉疲劳。我们使用 **高度（Elevation） = 亮度（Lightness）** 的原则。

### 调色板基准 (基于 Slate 色系)
*   **基础背景 (Background):** `#0F172A` (Slate 900) - 最底层的画布。
*   **一级卡片 (Surface - Low):** `#1E293B` (Slate 800) - 列表项、普通卡片。
*   **二级面板 (Surface - Medium):** `#334155` (Slate 700) - 弹窗、侧边栏、工具栏。
*   **高光悬浮 (Surface - High):** `#475569` (Slate 600) - 提示框、下拉菜单 (Dropdown)。

> [!TIP]
> **深色模式的阴影失效问题**：
> 在浅色模式下，我们用阴影表达层级；但在深色模式下，阴影往往看不见。因此，**在深色模式下，层级越高，背景色越亮**，并配合极细的浅色边框来划定边界。

### 边框与分割线 (Borders & Dividers)
使用极细的半透明白色边框，替代生硬的灰色线条，能更好融入不同背景。
*   **默认分割线:** `Color.White.copy(alpha = 0.1f)`
*   **卡片高亮边缘:** 给所有浮动面板加上 `BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))`。这种被称为 "Light catch"（接光面），能极大提升质感。

---

## 2. 材质与深度 (Materials & Depth - Apple Design)

深色模式不应该是“死气沉沉的黑块”，而应该像现实中的暗色玻璃。

*   **毛玻璃效果 (Glassmorphism):** 
    顶部的导航栏 (TopAppBar) 和底部的工具栏 (BottomAppBar) **不要使用纯色**，使用半透明材质。
    ```kotlin
    Modifier
        .background(Color(0xFF0F172A).copy(alpha = 0.7f)) // 70% 透明度的背景色
        .blur(radius = 20.dp) // 毛玻璃模糊
    ```
*   **视觉层次 (Vibrancy):** 当文字或图标叠加在毛玻璃上时，确保其具有足够的对比度。可以使用轻微的文字阴影或加粗字体来保证可读性。

---

## 3. 文字与排印 (Typography)

深色背景上的浅色文字，由于光晕效应（Halation），视觉上会显得比实际更粗、更拥挤。

*   **对比度控制 (Text Colors):**
    *   **主标题 (Primary):** `#F8FAFC` (Slate 50) - **不要用纯白 `#FFFFFF`**，太刺眼。
    *   **正文/副标题 (Secondary):** `#94A3B8` (Slate 400) - 阅读最舒适的对比度。
    *   **占位符/禁用 (Disabled):** `#475569` (Slate 600)。
*   **字距与字重微调:**
    *   在深色模式下，大标题的字重可以稍微降低（如 Bold 改为 SemiBold）。
    *   增加非常微小的字距（Letter Spacing），例如 `letterSpacing = 0.5.sp`，防止发光导致字母糊在一起。

---

## 4. 主题色与强调色 (Primary Accents)

浅色模式下的高饱和度颜色，直接搬到深色模式会“闪瞎眼”并产生视觉震颤 (Visual vibration)。

*   **降低饱和度 (Desaturation):** 调高主色的明度，降低饱和度。
    *   *错误 (浅色模式主色):* 纯蓝 `#0066FF`
    *   *正确 (深色模式主色):* 柔和的浅蓝 `#60A5FA` (Blue 400) 或 `#93C5FD` (Blue 300)。
*   **状态反馈 (Status Colors):**
    *   Success: `#34D399` (Emerald 400)
    *   Error: `#F87171` (Red 400)
    *   Warning: `#FBBF24` (Amber 400)

---

## 5. 交互与动效 (Interaction & Motion - Apple Design)

深色模式下，动画和反馈更容易引起注意，因此必须更加克制和顺滑。

*   **点击反馈 (Feedback):** 
    点击列表项时，与其使用强烈的 Ripple (水波纹)，不如使用底层变亮的缩放效果。
    ```kotlin
    // Apple Style 按压反馈
    .pointerInput(Unit) {
        detectTapGestures(
            onPress = { 
                // 动画缩放至 0.97f，并稍微提亮背景
            }
        )
    }
    ```
*   **弹簧动效 (Springs over Durations):** 
    任何弹出的面板、抽屉、对话框，必须使用弹簧动画 (Spring Animation)，而不是固定的 Tween/线性时间。
    *   Compose 中使用: `spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)`
    *   避免突然停止，保留运动的惯性。
*   **从源头展开 (Spatial Consistency):** 
    在编辑器中点击一个工具弹出的面板，它的动画起点（Transform Origin）必须是那个被点击的工具按钮，而不是屏幕中央凭空出现。

---

## 6. Compose 落地检查清单 (Checklist)

> [!IMPORTANT]
> 在开发 `featureEditor` 模块时，请严格对照以下清单：

- [ ] 是否彻底消灭了纯黑 (`#000000`) 和纯白 (`#FFFFFF`) 的直接使用？
- [ ] 浮动卡片、对话框是否加上了 `Color.White.copy(alpha = 0.05f)` 的极细边框？
- [ ] 顶部导航和底部工具栏是否使用了带有透明度的 Blur (毛玻璃) 效果？
- [ ] 强调色（按钮、图标选中的颜色）是否比浅色模式更柔和、饱和度更低？
- [ ] 所有展开、关闭动画是否都改用了 `spring()` 而非 `tween()`？
- [ ] 交互元素的焦点状态 (Focus) 边缘是否清晰可见？
