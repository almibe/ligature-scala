/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.wander

import munit.FunSuite

class BindingsSuite extends FunSuite {
    val identifier = Identifier("test")
    val identifier2 = Identifier("test2")

    test("add single value and read", () => {
        const binding = new Binding();
        binding.bind(identifier, "this is a test");
        let res = binding.read(identifier);
        expect(res).to.be.eql("this is a test");
        expect(() => binding.read(identifier2)).to.throw();
    })

    test("test scoping", () => {
        const binding = new Binding();
        binding.bind(identifier, "this is a test");
        expect(binding.read(identifier)).to.be.eql("this is a test");

        binding.addScope();
        expect(binding.read(identifier)).to.be.eql("this is a test");
        binding.bind(identifier, "this is a test2");
        binding.bind(identifier2, "this is a test3");
        expect(binding.read(identifier)).to.be.eql("this is a test2");
        expect(binding.read(identifier2)).to.be.eql("this is a test3");

        binding.removeScope()
        expect(binding.read(identifier)).to.be.eql("this is a test");
        expect(() => binding.read(identifier2)).to.throw();
    })
}
