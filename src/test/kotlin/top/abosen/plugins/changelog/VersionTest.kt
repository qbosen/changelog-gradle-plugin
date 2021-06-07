package top.abosen.plugins.changelog

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * @author  qiubaisen
 * @date  2021/6/8
 */
class VersionTest {
    @Test
    fun `parse normal version`() {
        assertEquals(Version(1, 0, 0), Version.parse("1.0.0"))
        assertEquals(Version(0, 1, 0), Version.parse("0.1.0"))
        assertEquals(Version(1, 10, 15), Version.parse("1.10.15"))
    }

    @Test
    fun `parse pre-release version`() {
        assertEquals(Version(1, 0, 0, "alpha"), Version.parse("1.0.0-alpha"))
        assertEquals(Version(1, 0, 0, "alpha.1"), Version.parse("1.0.0-alpha.1"))
        assertEquals(Version(1, 0, 0, "0.3.7"), Version.parse("1.0.0-0.3.7"))
        assertEquals(Version(1, 0, 0, "x.7.z.92"), Version.parse("1.0.0-x.7.z.92"))
    }

    @Test
    fun `parse build-meta version`() {
        assertEquals(Version(1, 0, 0, "alpha", "001"), Version.parse("1.0.0-alpha+001"))
        assertEquals(Version(1, 0, 0, buildMeta = "20130313144700"), Version.parse("1.0.0+20130313144700"))
        assertEquals(Version(1, 0, 0, "beta", "exp.sha.5114f85"), Version.parse("1.0.0-beta+exp.sha.5114f85"))
        assertEquals(Version(1, 0, 0, buildMeta = "21AF26D3--117B344092BD"), Version.parse("1.0.0+21AF26D3--117B344092BD"))
    }

    @Test
    fun `illegal semantic version`() {
        assertFails { Version.parse("01.0.0") }
        assertFails { Version.parse("01.0.0-µ") }
    }
}